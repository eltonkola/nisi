import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

public val gridIcon: ImageVector
	get() {
		if (_icon != null) {
			return _icon!!
		}
		_icon = ImageVector.Builder(
            name = "LayoutGrid",
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
				moveTo(4f, 3f)
				horizontalLineTo(9f)
				arcTo(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 10f, 4f)
				verticalLineTo(9f)
				arcTo(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 9f, 10f)
				horizontalLineTo(4f)
				arcTo(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 3f, 9f)
				verticalLineTo(4f)
				arcTo(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 4f, 3f)
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
				moveTo(15f, 3f)
				horizontalLineTo(20f)
				arcTo(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 21f, 4f)
				verticalLineTo(9f)
				arcTo(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 20f, 10f)
				horizontalLineTo(15f)
				arcTo(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 14f, 9f)
				verticalLineTo(4f)
				arcTo(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 15f, 3f)
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
				moveTo(15f, 14f)
				horizontalLineTo(20f)
				arcTo(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 21f, 15f)
				verticalLineTo(20f)
				arcTo(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 20f, 21f)
				horizontalLineTo(15f)
				arcTo(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 14f, 20f)
				verticalLineTo(15f)
				arcTo(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 15f, 14f)
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
				moveTo(4f, 14f)
				horizontalLineTo(9f)
				arcTo(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 10f, 15f)
				verticalLineTo(20f)
				arcTo(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 9f, 21f)
				horizontalLineTo(4f)
				arcTo(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 3f, 20f)
				verticalLineTo(15f)
				arcTo(1f, 1f, 0f, isMoreThanHalf = false, isPositiveArc = true, 4f, 14f)
				close()
			}
		}.build()
		return _icon!!
	}

private var _icon: ImageVector? = null
