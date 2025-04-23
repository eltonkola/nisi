import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val lockIcon: ImageVector
	get() {
		if (_undefined != null) {
			return _undefined!!
		}
		_undefined = ImageVector.Builder(
            name = "LockKeyhole",
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
				moveTo(13f, 16f)
				arcTo(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 12f, 17f)
				arcTo(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 11f, 16f)
				arcTo(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 13f, 16f)
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
				moveTo(5f, 10f)
				horizontalLineTo(19f)
				arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 21f, 12f)
				verticalLineTo(20f)
				arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 19f, 22f)
				horizontalLineTo(5f)
				arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 3f, 20f)
				verticalLineTo(12f)
				arcTo(2f, 2f, 0f, isMoreThanHalf = false, isPositiveArc = true, 5f, 10f)
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
				moveTo(7f, 10f)
				verticalLineTo(7f)
				arcToRelative(5f, 5f, 0f, isMoreThanHalf = false, isPositiveArc = true, 10f, 0f)
				verticalLineToRelative(3f)
			}
		}.build()
		return _undefined!!
	}

private var _undefined: ImageVector? = null
