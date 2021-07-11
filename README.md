# AnimLogoView

##简介
可用于欢迎界面展现Logo以及文字 每个文字以随机坐标移动到正中间 同时图片随进度从透明逐渐显现

##示范GIF
<img src="https://github.com/zz010625/AnimLogoView/blob/master/gif/Screenrecorder-2021-07-11-22-50-41-302_0_.gif" width="40%" height="40%" alt=" "/><br/>

##常用设置参数方法
setLogoTexts 设置Logo文字
setPicPath 设置Logo图标地址R.mipmap.xxx
setTextPadding 设置文字间距 默认为0
setTextSize 设置文字大小 默认为25dp
setPicWidthAndHeight 设置Logo图标的宽高 默认为图标自身宽高
setPicPaddingBottom 设置Logo图标距离Logo文字的距离(图标在上) 默认为0
setAnimDuration 设置动画执行时间 默认1500ms
setFinishDelayed 设置动画结束后延时时间 默认500ms
setOnAnimFinish 设置动画结束后的回调方法
