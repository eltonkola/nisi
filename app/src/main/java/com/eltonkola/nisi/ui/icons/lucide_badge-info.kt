import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val infoIcon: ImageVector
	get() {
		if (_icon != null) {
			return _icon!!
		}
		_icon = ImageVector.Builder(
            name = "BadgeInfo",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
			path(
    			fill = null,
    			fillAlpha = 1.0f,
    			stroke = SolidColor(Color(0xFF000000)),
    			strokeAlpha = 1.0f,
    			strokeLineWidth = 2f,
    			strokeLineCap = StrokeCap.Round,
    			strokeLineJoin = StrokeJoin.Round,
    			strokeLineMiter = 1.0f,
    			pathFillType = PathFillType.NonZero
			) {
				moveTo(3.85f, 8.62f)
				arcToRelative(4f, 4f, 0f, isMoreThanHalf = false, isPositiveArc = true, 4.78f, -4.77f)
				arcToRelative(4f, 4f, 0f, isMoreThanHalf = false, isPositiveArc = true, 6.74f, 0f)
				arcToRelative(4f, 4f, 0f, isMoreThanHalf = false, isPositiveArc = true, 4.78f, 4.78f)
				arcToRelative(4f, 4f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0f, 6.74f)
				arcToRelative(4f, 4f, 0f, isMoreThanHalf = false, isPositiveArc = true, -4.77f, 4.78f)
				arcToRelative(4f, 4f, 0f, isMoreThanHalf = false, isPositiveArc = true, -6.75f, 0f)
				arcToRelative(4f, 4f, 0f, isMoreThanHalf = false, isPositiveArc = true, -4.78f, -4.77f)
				arcToRelative(4f, 4f, 0f, isMoreThanHalf = false, isPositiveArc = true, 0f, -6.76f)
				close()
			}
			path(
    			fill = null,
    			fillAlpha = 1.0f,
    			stroke = SolidColor(Color(0xFF000000)),
    			strokeAlpha = 1.0f,
    			strokeLineWidth = 2f,
    			strokeLineCap = StrokeCap.Round,
    			strokeLineJoin = StrokeJoin.Round,
    			strokeLineMiter = 1.0f,
    			pathFillType = PathFillType.NonZero
			) {
				moveTo(12f, 16f)
				lineTo(12f, 12f)
			}
			path(
    			fill = null,
    			fillAlpha = 1.0f,
    			stroke = SolidColor(Color(0xFF000000)),
    			strokeAlpha = 1.0f,
    			strokeLineWidth = 2f,
    			strokeLineCap = StrokeCap.Round,
    			strokeLineJoin = StrokeJoin.Round,
    			strokeLineMiter = 1.0f,
    			pathFillType = PathFillType.NonZero
			) {
				moveTo(12f, 8f)
				lineTo(12.01f, 8f)
			}
		}.build()
		return _icon!!
	}

private var _icon: ImageVector? = null
