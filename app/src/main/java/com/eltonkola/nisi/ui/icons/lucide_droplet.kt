import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val iconDroplet: ImageVector
	get() {
		if (_undefined != null) {
			return _undefined!!
		}
		_undefined = ImageVector.Builder(
            name = "Droplet",
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
				moveTo(12f, 22f)
				arcToRelative(7f, 7f, 0f, isMoreThanHalf = false, isPositiveArc = false, 7f, -7f)
				curveToRelative(0f, -2f, -1f, -3.9f, -3f, -5.5f)
				reflectiveCurveToRelative(-3.5f, -4f, -4f, -6.5f)
				curveToRelative(-0.5f, 2.5f, -2f, 4.9f, -4f, 6.5f)
				curveTo(6f, 11.1f, 5f, 13f, 5f, 15f)
				arcToRelative(7f, 7f, 0f, isMoreThanHalf = false, isPositiveArc = false, 7f, 7f)
				close()
			}
		}.build()
		return _undefined!!
	}

private var _undefined: ImageVector? = null
