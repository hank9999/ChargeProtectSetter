# ChargeProtectSetter
一个自定义全局电池电量保护值的 Xposed 模块   

> [!WARNING]  
> 如因使用本模块造成的一切后果, 项目组概不负责, 包括但不限于设备损坏、数据丢失等, 使用者需自行承担风险。  
> 本项目不对任何任何衍生项目负责。

## 使用条件
目前仅测试了 HyperOS 2, 其他版本可能会有问题  
在`省电与电池-电池保护`页面中, 必须有`电池健康保护`选项  

## 使用方法
安装 Xposed 模块后, 进入`省电与电池-电池保护`页面, 启用`电池健康保护`, 重启作用域或手机即可  

## 使用效果
如果配置为 75%  
全部 80% 的地方都改为 75%  
充电电量最高到 75%, `电池保护` 页面 UI 显示 75%, 通知提示显示 75%  

## 配置方法
无配置文件默认为 80%  
如需调整充电限制, 需要修改 `/Android/data/com.miui.securitycenter/files/ChargeProtectSetter/charge_level` 文件, 不存在需要创建一个  
直接填写电量数字, 没有百分号，没有换行  
比如 调整至 75%, 则文件内容为 `75`  
为了保持电池安全/健康, 限制范围不低于 15%, 不超过 95%  
**电池健康的循环范围是 20%-80%, 超出范围导致的一切问题 由使用者自行承担责任, 项目组概不负责**

## 致谢
- [HookTool](https://github.com/HChenX/HookTool)  
- [DexKit](https://github.com/luckypray/DexKit)  

特此感谢 焕晨的 [AutoSEffSwitch](https://github.com/HChenX/AutoSEffSwitch) 项目, 这是本人的第一个 Xposed 模块, 整体代码结构参考了 AutoSEffSwitch, 由衷感谢

## 许可协议
[GPL-3.0 (GNU GENERAL PUBLIC LICENSE version 3)](LICENSE)