# XRay-Forge

可绕假矿的 Minecraft 矿物透视 Mod

## 重要 IMPORTANT

本项目大部分代码来自 [MichaelHillcox/XRay-Mod](https://github.com/MichaelHillcox/XRay-Mod) 和 [tokfrans03/AntiAntiXray](https://github.com/tokfrans03/AntiAntiXray)，本人只是对接了功能，并非所有代码皆为我原创。

Most of the codes of this project come from [MichaelHillcox/XRay-Mod](https://github.com/MichaelHillcox/XRay-Mod) and [tokfrans03/AntiAntiXray](https://github.com/tokfrans03/AntiAntiXray). I just connected the functions. Not all the codes are my original.

## 编译 Build

不发 Releases，自己编译去

No Releases, please build it for youself.

```
./gradlew clean build
```

## 用法 Usage

只能解除距离你4格距离内的假矿(范围可以在Mod设置调，但原理是发包，范围越大耗时越长)按下 `\` 键开启矿物透视，到一个看起来很多假矿的地方按下 `G` 键开始发包并保持位置不动，等待右上角的读条结束之后周围的真矿就出来了，按下 `N` 键冻结当前矿透结果，过去挖完后再次按下 `N` 键可解除冻结。然后你就可以进行下一轮的绕假矿了。

It just can get rid off the fake ores which have 4 blocks distance away from you. (distance can be set in mod settings, but mod works depend on sending packets. It maybe take a long time if you increase the distance) Press `\` key to toggle xray, you will see many fake ores. Just surrounded the fake ores, press `G` key and stay for a short time. Waiting the loading in the upper right corner done, the true ores come out. Press `N` key to freeze the xray result and dig them. Press `N` key again to cancel freeze. And enjoy the next round of the anti-anti-xray.

按下 `Z` 可以打开矿物选择菜单，Shift+点击左侧的矿物可以切换开启或禁用，其他按钮都有文字，懂的都懂。

Press `Z` key to open the ores select menu, Press Shift+Mouse Left the ore to switch enable or disable its display. Every button has it's text, everyone who knows knows knows.