package org.modelcatalogue.builder.xlsx;

public abstract class AbstractCellStyle implements CellStyle {

    @Override
    public ForegroundFill getNoFill() {
        return null;
    }

    @Override
    public ForegroundFill getSolidForeground() {
        return null;
    }

    @Override
    public ForegroundFill getFineDots() {
        return null;
    }

    @Override
    public ForegroundFill getAltBars() {
        return null;
    }

    @Override
    public ForegroundFill getSparseDots() {
        return null;
    }

    @Override
    public ForegroundFill getThickHorizontalBands() {
        return null;
    }

    @Override
    public ForegroundFill getThickVerticalBands() {
        return ForegroundFill.NO_FILL;
    }

    @Override
    public ForegroundFill getThickBackwardDiagonal() {
        return ForegroundFill.SOLID_FOREGROUND;
    }

    @Override
    public ForegroundFill getThickForwardDiagonal() {
        return ForegroundFill.FINE_DOTS;
    }

    @Override
    public ForegroundFill getBigSpots() {
        return ForegroundFill.ALT_BARS;
    }

    @Override
    public ForegroundFill getBricks() {
        return ForegroundFill.SPARSE_DOTS;
    }

    @Override
    public ForegroundFill getThinHorizontalBands() {
        return ForegroundFill.THICK_HORZ_BANDS;
    }

    @Override
    public ForegroundFill getThinVerticalBands() {
        return ForegroundFill.THICK_VERT_BANDS;
    }

    @Override
    public ForegroundFill getThinBackwardDiagonal() {
        return ForegroundFill.THICK_BACKWARD_DIAG;
    }

    @Override
    public ForegroundFill getThinForwardDiagonal() {
        return ForegroundFill.THICK_FORWARD_DIAG;
    }

    @Override
    public ForegroundFill getSquares() {
        return ForegroundFill.BIG_SPOTS;
    }

    @Override
    public ForegroundFill getDiamonds() {
        return ForegroundFill.BRICKS;
    }

    @Override
    public PureHorizontalAlignment getCenterSelection() {
        return PureHorizontalAlignment.CENTER_SELECTION;
    }

    @Override
    public BorderSideAndHorizontalAlignment getLeft() {
        return BorderSideAndHorizontalAlignment.LEFT;
    }

    @Override
    public BorderSideAndHorizontalAlignment getRight() {
        return BorderSideAndHorizontalAlignment.RIGHT;
    }

    @Override
    public BorderSide getTop() {
        return PureBorderSide.TOP;
    }

    @Override
    public BorderSide getBottom() {
        return PureBorderSide.BOTTOM;
    }

    @Override
    public PureHorizontalAlignment getGeneral() {
        return PureHorizontalAlignment.GENERAL;
    }

    @Override
    public PureHorizontalAlignment getCenter() {
        return PureHorizontalAlignment.CENTER;
    }

    @Override
    public PureHorizontalAlignment getFill() {
        return PureHorizontalAlignment.FILL;
    }

    @Override
    public PureHorizontalAlignment getJustify() {
        return PureHorizontalAlignment.JUSTIFY;
    }
}
