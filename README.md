# ChargeProtectSetter
一个将全局电池电量保护值从 80% 调整至 90% 的 Xposed 模块   

> [!WARNING]  
> 如因使用本模块造成的一切后果, 项目组概不负责, 包括但不限于设备损坏、数据丢失等, 使用者需自行承担风险。  
> 本项目不对任何任何衍生项目负责。

## 使用条件
目前仅测试了 HyperOS 2, 其他版本可能会有问题  
在`省电与电池-电池保护`页面中, 必须有`电池健康保护`选项  

## 使用方法
安装 Xposed 模块后, 进入`省电与电池-电池保护`页面, 启用`电池健康保护`, 重启作用域或手机即可  

## 使用效果
全部 80% 的地方都改为 90%  
充电电量最高到 90%, `电池保护` 页面 UI 显示 90%, 通知提示显示 90%  

## 致谢
- [HookTool](https://github.com/HChenX/HookTool)  
- [DexKit](https://github.com/luckypray/DexKit)  

特此感谢 焕晨的 [AutoSEffSwitch](https://github.com/HChenX/AutoSEffSwitch) 项目, 这是本人的第一个 Xposed 模块, 整体代码结构参考了 AutoSEffSwitch, 由衷感谢

## 许可协议
[GPL-3.0 (GNU GENERAL PUBLIC LICENSE version 3)](LICENSE)