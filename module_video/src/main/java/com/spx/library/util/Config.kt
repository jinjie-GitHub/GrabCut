package com.spx.library.util

class Config {
    companion object {
        // message消息
        var MSG_UPDATE = 0

        // 对于长视频, 每隔3s截取一个缩略图
        val MAX_FRAME_INTERVAL_MS = 3 * 1000

        // 默认显示10个缩略图
        val DEFAULT_FRAME_COUNT = 10

        // 裁剪最小时间为1s
        val minSelection = 1 * 1000 // 最短1s

        // 裁剪最长时间为 1小时
        val maxSelection: Long = 60 * 60 * 1000 // 最长1小时
    }
}