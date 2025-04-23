import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val iconPreferences: ImageVector
	get() {
		if (_icon != null) {
			return _icon!!
		}
		_icon = ImageVector.Builder(
            name = "Settings2",
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
				moveTo(20f, 7f)
				horizontalLineToRelative(-9f)
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
				moveTo(14f, 17f)
				horizontalLineTo(5f)
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
				moveTo(20f, 17f)
				arcTo(3f, 3f, 0f, isMoreThanHalf = false, isPositiveArc = true, 17f, 20f)
				arcTo(3f, 3f, 0f, isMoreThanHalf = false, isPositiveArc = true, 14f, 17f)
				arcTo(3f, 3f, 0f, isMoreThanHalf = false, isPositiveArc = true, 20f, 17f)
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
				moveTo(10f, 7f)
				arcTo(3f, 3f, 0f, isMoreThanHalf = false, isPositiveArc = true, 7f, 10f)
				arcTo(3f, 3f, 0f, isMoreThanHalf = false, isPositiveArc = true, 4f, 7f)
				arcTo(3f, 3f, 0f, isMoreThanHalf = false, isPositiveArc = true, 10f, 7f)
				close()
			}
		}.build()
		return _icon!!
	}

private var _icon: ImageVector? = null
