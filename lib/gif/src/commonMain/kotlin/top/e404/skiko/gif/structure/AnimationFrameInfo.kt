package top.e404.skiko.gif.structure


data class AnimationFrameInfo(
    var duration: Int = 1000,
    var disposalMethod: AnimationDisposalMode = AnimationDisposalMode.UNUSED,
)
