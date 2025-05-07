import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val iconDelete: ImageVector
	get() {
		if (_undefined != null) {
			return _undefined!!
		}
		_undefined = ImageVector.Builder(
            name = "Trash2",
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
				moveTo(3f, 6f)
				horizontalLineToRelative(18f)
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
				moveTo(19f, 6f)
				verticalLineToRelative(14f)
				curveToRelative(0f, 1f, -1f, 2f, -2f, 2f)
				horizontalLineTo(7f)
				curveToRelative(-1f, 0f, -2f, -1f, -2f, -2f)
				verticalLineTo(6f)
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
				moveTo(8f, 6f)
				verticalLineTo(4f)
				curveToRelative(0f, -1f, 1f, -2f, 2f, -2f)
				horizontalLineToRelative(4f)
				curveToRelative(1f, 0f, 2f, 1f, 2f, 2f)
				verticalLineToRelative(2f)
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
				moveTo(10f, 11f)
				lineTo(10f, 17f)
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
				moveTo(14f, 11f)
				lineTo(14f, 17f)
			}
		}.build()
		return _undefined!!
	}

private var _undefined: ImageVector? = null
