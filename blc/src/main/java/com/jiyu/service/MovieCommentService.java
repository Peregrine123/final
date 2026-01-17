package com.jiyu.service;

import com.jiyu.dao.MovieCommentDAO;
import com.jiyu.dao.MovieCommentLikeDAO;
import com.jiyu.dao.MovieDAO;
import com.jiyu.dto.MovieCommentDto;
import com.jiyu.dto.MovieCommentPageDto;
import com.jiyu.dto.RatingSummaryDto;
import com.jiyu.pojo.Movie;
import com.jiyu.pojo.MovieComment;
import com.jiyu.pojo.MovieCommentStatus;
import com.jiyu.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Pattern;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Service
public class MovieCommentService {

    private static final Pattern HTML_TAG_PATTERN = Pattern.compile("<[^>]*>");

    @Value("${blc.comment.max-length:280}")
    private int maxLength;

    @Value("${blc.comment.rate-limit-seconds:10}")
    private long rateLimitSeconds;

    /**
     * Comma-separated keyword list. Example: "badword,fuck,傻逼"
     */
    @Value("${blc.comment.blocked-words:}")
    private String blockedWordsRaw;

    @Autowired
    MovieCommentDAO movieCommentDAO;

    @Autowired
    MovieCommentLikeDAO movieCommentLikeDAO;

    @Autowired
    MovieDAO movieDAO;

    @Autowired
    UserMovieStatusService userMovieStatusService;

    private List<String> blockedWords() {
        String raw = blockedWordsRaw == null ? "" : blockedWordsRaw.trim();
        if (raw.isEmpty()) {
            return new ArrayList<>();
        }
        List<String> words = new ArrayList<>();
        for (String w : Arrays.asList(raw.split(","))) {
            if (w == null) continue;
            String normalized = w.trim();
            if (!normalized.isEmpty()) {
                words.add(normalized.toLowerCase(Locale.ROOT));
            }
        }
        return words;
    }

    private void enforceRateLimit(User user) {
        long seconds = Math.max(0, rateLimitSeconds);
        if (seconds <= 0 || user == null) {
            return;
        }
        Optional<MovieComment> last = movieCommentDAO.findFirstByUser_IdOrderByUpdatedAtDesc(user.getId());
        if (!last.isPresent()) {
            return;
        }
        Instant lastAt = last.get().getUpdatedAt();
        if (lastAt == null) {
            return;
        }
        Instant now = Instant.now();
        if (now.isBefore(lastAt.plusSeconds(seconds))) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "操作过于频繁，请稍后再试");
        }
    }

    private String sanitize(String content) {
        if (content == null) {
            return null;
        }
        String trimmed = content.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        // Minimal XSS protection: strip all HTML tags.
        String noTags = HTML_TAG_PATTERN.matcher(trimmed).replaceAll("");
        String normalized = noTags.trim();
        return normalized.isEmpty() ? null : normalized;
    }

    private void validate(Integer rating, String sanitizedContent) {
        boolean hasRating = rating != null;
        boolean hasContent = sanitizedContent != null && !sanitizedContent.isEmpty();
        if (!hasRating && !hasContent) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "评分或短评至少填写一项");
        }
        if (hasRating && (rating < 1 || rating > 5)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "评分必须为 1-5");
        }
        if (hasContent) {
            int max = Math.max(1, maxLength);
            if (sanitizedContent.length() > max) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "短评内容过长");
            }
            List<String> blocked = blockedWords();
            if (!blocked.isEmpty()) {
                String lower = sanitizedContent.toLowerCase(Locale.ROOT);
                for (String w : blocked) {
                    if (w.isEmpty()) continue;
                    if (lower.contains(w)) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "短评内容不合规");
                    }
                }
            }
        }
    }

    private MovieCommentDto toDto(MovieComment mc) {
        if (mc == null) {
            return null;
        }
        int movieId = mc.getMovie() == null ? 0 : mc.getMovie().getId();
        long userId = mc.getUser() == null ? 0 : mc.getUser().getId();
        String username = mc.getUser() == null ? null : mc.getUser().getUsername();
        return new MovieCommentDto(
                mc.getId(),
                movieId,
                userId,
                username,
                mc.getRating(),
                mc.getContent(),
                mc.getStatus(),
                mc.getCreatedAt(),
                mc.getUpdatedAt(),
                0L,
                false
        );
    }

    private void fillLikes(User user, MovieCommentDto dto) {
        if (dto == null) {
            return;
        }
        long commentId = dto.getId();
        dto.setLikeCount(movieCommentLikeDAO.countByComment_Id(commentId));
        dto.setLikedByMe(user != null && movieCommentLikeDAO.existsByComment_IdAndUser_Id(commentId, user.getId()));
    }

    @Transactional
    public MovieCommentDto upsertMyComment(User user, int movieId, Integer rating, String content) {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登录或登录已过期");
        }
        String sanitizedContent = sanitize(content);
        validate(rating, sanitizedContent);

        Movie movie = movieDAO.findById(movieId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "电影不存在"));

        Optional<MovieComment> existingOpt = movieCommentDAO.findByMovie_IdAndUser_Id(movieId, user.getId());
        if (existingOpt.isPresent()) {
            MovieComment existing = existingOpt.get();
            boolean same = Objects.equals(existing.getRating(), rating)
                    && Objects.equals(existing.getContent(), sanitizedContent);
            if (same) {
                // Idempotent: keep original status/timestamps.
                userMovieStatusService.markWatched(user, movieId);
                MovieCommentDto dto = toDto(existing);
                fillLikes(user, dto);
                return dto;
            }
            enforceRateLimit(user);
            existing.setRating(rating);
            existing.setContent(sanitizedContent);
            // No-audit mode: publish immediately.
            existing.setStatus(MovieCommentStatus.APPROVED);
            movieCommentDAO.save(existing);
            userMovieStatusService.markWatched(user, movieId);
            MovieCommentDto dto = toDto(existing);
            fillLikes(user, dto);
            return dto;
        }

        enforceRateLimit(user);
        MovieComment created = new MovieComment();
        created.setUser(user);
        created.setMovie(movie);
        created.setRating(rating);
        created.setContent(sanitizedContent);
        // No-audit mode: publish immediately.
        created.setStatus(MovieCommentStatus.APPROVED);
        movieCommentDAO.save(created);
        userMovieStatusService.markWatched(user, movieId);
        MovieCommentDto dto = toDto(created);
        fillLikes(user, dto);
        return dto;
    }

    @Transactional(readOnly = true)
    public MovieCommentDto getMyComment(User user, int movieId) {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登录或登录已过期");
        }
        MovieComment mc = movieCommentDAO.findByMovie_IdAndUser_Id(movieId, user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "短评不存在"));
        MovieCommentDto dto = toDto(mc);
        fillLikes(user, dto);
        return dto;
    }

    @Transactional(readOnly = true)
    public MovieCommentPageDto listApprovedComments(User user, int movieId, int page, int size) {
        if (!movieDAO.existsById(movieId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "电影不存在");
        }
        int safePage = Math.max(1, page);
        int safeSize = Math.min(Math.max(1, size), 50);
        PageRequest pr = PageRequest.of(
                safePage - 1,
                safeSize,
                Sort.by(Sort.Direction.DESC, "createdAt").and(Sort.by(Sort.Direction.DESC, "id"))
        );
        Page<MovieComment> p = movieCommentDAO.findByMovie_Id(movieId, pr);

        List<Long> commentIds = new ArrayList<>();
        for (MovieComment mc : p.getContent()) {
            if (mc != null) {
                commentIds.add(mc.getId());
            }
        }
        Map<Long, Long> likeCounts = new HashMap<>();
        Set<Long> likedByMe = new HashSet<>();
        if (!commentIds.isEmpty()) {
            for (Object[] row : movieCommentLikeDAO.countLikesByCommentIds(commentIds)) {
                if (row == null || row.length < 2) continue;
                Number cidNum = row[0] instanceof Number ? (Number) row[0] : null;
                Number cntNum = row[1] instanceof Number ? (Number) row[1] : null;
                Long cid = cidNum == null ? null : cidNum.longValue();
                Long cnt = cntNum == null ? null : cntNum.longValue();
                if (cid != null) {
                    likeCounts.put(cid, cnt == null ? 0L : cnt);
                }
            }
            if (user != null) {
                likedByMe.addAll(movieCommentLikeDAO.findLikedCommentIds(user.getId(), commentIds));
            }
        }

        List<MovieCommentDto> items = new ArrayList<>();
        for (MovieComment mc : p.getContent()) {
            MovieCommentDto dto = toDto(mc);
            if (dto == null) {
                continue;
            }
            long cid = dto.getId();
            dto.setLikeCount(likeCounts.getOrDefault(cid, 0L));
            dto.setLikedByMe(likedByMe.contains(cid));
            items.add(dto);
        }
        return new MovieCommentPageDto(items, p.getTotalElements(), safePage, safeSize);
    }

    @Transactional(readOnly = true)
    public RatingSummaryDto getRatingSummary(int movieId) {
        if (!movieDAO.existsById(movieId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "电影不存在");
        }
        long count = movieCommentDAO.countApprovedRatings(movieId);
        Double avg = count > 0 ? movieCommentDAO.avgApprovedRating(movieId) : null;
        // avg might be null even if count>0 in some edge cases; keep it null-safe.
        return new RatingSummaryDto(avg, count);
    }

    @Transactional
    public MovieCommentDto setCommentStatus(long commentId, MovieCommentStatus status) {
        if (status == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "status 不能为空");
        }
        MovieComment mc = movieCommentDAO.findById(commentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "短评不存在"));
        if (mc.getStatus() != status) {
            mc.setStatus(status);
            movieCommentDAO.save(mc);
        }
        return toDto(mc);
    }

    public void requireAdmin(User user) {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "未登录或登录已过期");
        }
        String role = user.getRole();
        if (role == null || !role.equalsIgnoreCase("admin")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权限");
        }
    }
}
