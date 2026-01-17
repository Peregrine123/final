# WSL 开机自启 Redis（systemd 方案）

## 1) 关闭 WSL
在 Windows 终端 / PowerShell 执行：
```powershell
wsl.exe --shutdown
```

## 2) 重新打开 WSL 后启用服务
```bash
sudo systemctl enable --now redis-server
systemctl status redis-server --no-pager
```

## 3) 验证
```bash
redis-cli ping
```

如果 systemctl 仍不可用，把 `cat /etc/wsl.conf` 的输出发我，我再帮你调整。
