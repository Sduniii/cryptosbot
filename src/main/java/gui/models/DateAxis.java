package gui.models;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleLongProperty;
import javafx.scene.chart.Axis;
import javafx.util.Duration;
import javafx.util.StringConverter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


public final class DateAxis extends Axis<Date> {


    private final LongProperty currentLowerBound = new SimpleLongProperty(this, "currentLowerBound");

    private final LongProperty currentUpperBound = new SimpleLongProperty(this, "currentUpperBound");

    private final ObjectProperty<StringConverter<Date>> tickLabelFormatter = new ObjectPropertyBase<StringConverter<Date>>() {
        @Override
        protected void invalidated() {
            if (!isAutoRanging()) {
                invalidateRange();
                requestAxisLayout();
            }
        }

        @Override
        public Object getBean() {
            return DateAxis.this;
        }

        @Override
        public String getName() {
            return "tickLabelFormatter";
        }
    };


    private Date minDate, maxDate;

    private ObjectProperty<Date> lowerBound = new ObjectPropertyBase<Date>() {
        @Override
        protected void invalidated() {
            if (!isAutoRanging()) {
                invalidateRange();
                requestAxisLayout();
            }
        }

        @Override
        public Object getBean() {
            return DateAxis.this;
        }

        @Override
        public String getName() {
            return "lowerBound";
        }
    };

    private ObjectProperty<Date> upperBound = new ObjectPropertyBase<Date>() {
        @Override
        protected void invalidated() {
            if (!isAutoRanging()) {
                invalidateRange();
                requestAxisLayout();
            }
        }

        @Override
        public Object getBean() {
            return DateAxis.this;
        }

        @Override
        public String getName() {
            return "upperBound";
        }
    };

    private ChartLayoutAnimator animator = new ChartLayoutAnimator(this);

    private Object currentAnimationID;

    private DateAxis.Interval actualInterval = DateAxis.Interval.DECADE;

    public DateAxis() {
    }


    public DateAxis(Date lowerBound, Date upperBound) {
        this();
        setAutoRanging(false);
        setLowerBound(lowerBound);
        setUpperBound(upperBound);
    }

    public DateAxis(String axisLabel, Date lowerBound, Date upperBound) {
        this(lowerBound, upperBound);
        setLabel(axisLabel);
    }

    @Override
    public void invalidateRange(List<Date> list) {
        super.invalidateRange(list);

        Collections.sort(list);
        if (list.isEmpty()) {
            minDate = maxDate = new Date();
        } else if (list.size() == 1) {
            minDate = maxDate = list.get(0);
        } else if (list.size() > 1) {
            minDate = list.get(0);
            maxDate = list.get(list.size() - 1);
        }
    }

    @Override
    protected Object autoRange(double length) {
        if (isAutoRanging()) {
            return new Object[]{minDate, maxDate};
        } else {
            if (getLowerBound() == null || getUpperBound() == null) {
                throw new IllegalArgumentException("If autoRanging is false, a lower and upper bound must be set.");
            }
            return getRange();
        }
    }

    @Override
    protected void setRange(Object range, boolean animating) {
        Object[] r = (Object[]) range;
        Date oldLowerBound = getLowerBound();
        Date oldUpperBound = getUpperBound();
        Date lower = (Date) r[0];
        Date upper = (Date) r[1];
        setLowerBound(lower);
        setUpperBound(upper);

        if (animating) {


            animator.stop(currentAnimationID);
            currentAnimationID = animator.animate(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(currentLowerBound, oldLowerBound.getTime()),
                            new KeyValue(currentUpperBound, oldUpperBound.getTime())
                    ),
                    new KeyFrame(Duration.millis(700),
                            new KeyValue(currentLowerBound, lower.getTime()),
                            new KeyValue(currentUpperBound, upper.getTime())
                    )
            );

        } else {
            currentLowerBound.set(getLowerBound().getTime());
            currentUpperBound.set(getUpperBound().getTime());
        }
    }

    @Override
    protected Object getRange() {
        return new Object[]{getLowerBound(), getUpperBound()};
    }

    @Override
    public double getZeroPosition() {
        return 0;
    }

    @Override
    public double getDisplayPosition(Date date) {
        final double length = getSide().isHorizontal() ? getWidth() : getHeight();
        double diff = currentUpperBound.get() - currentLowerBound.get();
        double range = length - getZeroPosition();
        double d = (date.getTime() - currentLowerBound.get()) / diff;
        if (getSide().isVertical()) {
            return getHeight() - d * range + getZeroPosition();
        } else {
            return d * range + getZeroPosition();
        }
    }

    @Override
    public Date getValueForDisplay(double displayPosition) {
        final double length = getSide().isHorizontal() ? getWidth() : getHeight();
        double diff = currentUpperBound.get() - currentLowerBound.get();
        double range = length - getZeroPosition();

        if (getSide().isVertical()) {
            return new Date((long) ((displayPosition - getZeroPosition() - getHeight()) / -range * diff + currentLowerBound.get()));
        } else {
            return new Date((long) ((displayPosition - getZeroPosition()) / range * diff + currentLowerBound.get()));
        }
    }

    @Override
    public boolean isValueOnAxis(Date date) {
        return date.getTime() > currentLowerBound.get() && date.getTime() < currentUpperBound.get();
    }

    @Override
    public double toNumericValue(Date date) {
        return date.getTime();
    }

    @Override
    public Date toRealValue(double v) {
        return new Date((long) v);
    }

    @Override
    protected List<Date> calculateTickValues(double v, Object range) {
        Object[] r = (Object[]) range;
        Date lower = (Date) r[0];
        Date upper = (Date) r[1];

        List<Date> dateList = new ArrayList<Date>();
        Calendar calendar = Calendar.getInstance();

        // The preferred gap which should be between two tick marks.
        double averageTickGap = 100;
        double averageTicks = v / averageTickGap;

        List<Date> previousDateList = new ArrayList<Date>();

        DateAxis.Interval previousInterval = DateAxis.Interval.values()[0];

        // Starting with the greatest interval, add one of each calendar unit.
        for (DateAxis.Interval interval : DateAxis.Interval.values()) {
            // Reset the calendar.
            calendar.setTime(lower);
            dateList.clear();
            previousDateList.clear();
            actualInterval = interval;

            while (calendar.getTime().getTime() <= upper.getTime()) {
                dateList.add(calendar.getTime());
                calendar.add(interval.interval, interval.amount);
            }

            if (dateList.size() > averageTicks) {
                calendar.setTime(lower);
                // Recheck if the previous interval is better suited.
                while (calendar.getTime().getTime() <= upper.getTime()) {
                    previousDateList.add(calendar.getTime());
                    calendar.add(previousInterval.interval, previousInterval.amount);
                }
                break;
            }

            previousInterval = interval;
        }
        if (previousDateList.size() - averageTicks > averageTicks - dateList.size()) {
            dateList = previousDateList;
            actualInterval = previousInterval;
        }

        // At last add the upper bound.
        dateList.add(upper);

        List<Date> evenDateList = makeDatesEven(dateList, calendar);
        if (evenDateList.size() > 2) {

            Date secondDate = evenDateList.get(1);
            Date thirdDate = evenDateList.get(2);
            Date lastDate = evenDateList.get(dateList.size() - 2);
            Date previousLastDate = evenDateList.get(dateList.size() - 3);


            if (secondDate.getTime() - lower.getTime() < (thirdDate.getTime() - secondDate.getTime()) / 2) {
                evenDateList.remove(secondDate);
            }

            if (upper.getTime() - lastDate.getTime() < (lastDate.getTime() - previousLastDate.getTime()) / 2) {
                evenDateList.remove(lastDate);
            }
        }

        return evenDateList;
    }

    @Override
    protected void layoutChildren() {
        if (!isAutoRanging()) {
            currentLowerBound.set(getLowerBound().getTime());
            currentUpperBound.set(getUpperBound().getTime());
        }
        super.layoutChildren();
    }

    @Override
    protected String getTickMarkLabel(Date date) {

        StringConverter<Date> converter = getTickLabelFormatter();
        if (converter != null) {
            return converter.toString(date);
        }

        DateFormat dateFormat;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        if (actualInterval.interval == Calendar.YEAR && calendar.get(Calendar.MONTH) == 0 && calendar.get(Calendar.DATE) == 1) {
            dateFormat = new SimpleDateFormat("yyyy");
        } else if (actualInterval.interval == Calendar.MONTH && calendar.get(Calendar.DATE) == 1) {
            dateFormat = new SimpleDateFormat("MMM yy");
        } else {
            switch (actualInterval.interval) {
                case Calendar.DATE:
                case Calendar.WEEK_OF_YEAR:
                default:
                    dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM);
                    break;
                case Calendar.HOUR:
                case Calendar.MINUTE:
                    dateFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
                    break;
                case Calendar.SECOND:
                    dateFormat = DateFormat.getTimeInstance(DateFormat.MEDIUM);
                    break;
                case Calendar.MILLISECOND:
                    dateFormat = DateFormat.getTimeInstance(DateFormat.FULL);
                    break;
            }
        }
        return dateFormat.format(date);
    }


    private List<Date> makeDatesEven(List<Date> dates, Calendar calendar) {

        if (dates.size() > 2) {
            List<Date> evenDates = new ArrayList<Date>();
            for (int i = 0; i < dates.size(); i++) {
                calendar.setTime(dates.get(i));
                switch (actualInterval.interval) {
                    case Calendar.YEAR:
                        if (i != 0 && i != dates.size() - 1) {
                            calendar.set(Calendar.MONTH, 0);
                            calendar.set(Calendar.DATE, 1);
                        }
                        calendar.set(Calendar.HOUR_OF_DAY, 0);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 6);
                        break;
                    case Calendar.MONTH:
                        if (i != 0 && i != dates.size() - 1) {
                            calendar.set(Calendar.DATE, 1);
                        }
                        calendar.set(Calendar.HOUR_OF_DAY, 0);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 5);
                        break;
                    case Calendar.WEEK_OF_YEAR:
                        // Make weeks begin with first day of week?
                        calendar.set(Calendar.HOUR_OF_DAY, 0);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 4);
                        break;
                    case Calendar.DATE:
                        calendar.set(Calendar.HOUR_OF_DAY, 0);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 3);
                        break;
                    case Calendar.HOUR:
                        if (i != 0 && i != dates.size() - 1) {
                            calendar.set(Calendar.MINUTE, 0);
                            calendar.set(Calendar.SECOND, 0);
                        }
                        calendar.set(Calendar.MILLISECOND, 2);
                        break;
                    case Calendar.MINUTE:
                        if (i != 0 && i != dates.size() - 1) {
                            calendar.set(Calendar.SECOND, 0);
                        }
                        calendar.set(Calendar.MILLISECOND, 1);
                        break;
                    case Calendar.SECOND:
                        calendar.set(Calendar.MILLISECOND, 0);
                        break;

                }
                evenDates.add(calendar.getTime());
            }

            return evenDates;
        } else {
            return dates;
        }
    }

    public final ObjectProperty<Date> lowerBoundProperty() {
        return lowerBound;
    }

    public final Date getLowerBound() {
        return lowerBound.get();
    }

    public final void setLowerBound(Date date) {
        lowerBound.set(date);
    }

    public final ObjectProperty<Date> upperBoundProperty() {
        return upperBound;
    }

    public final Date getUpperBound() {
        return upperBound.get();
    }

    public final void setUpperBound(Date date) {
        upperBound.set(date);
    }

    public final StringConverter<Date> getTickLabelFormatter() {
        return tickLabelFormatter.getValue();
    }

    public final void setTickLabelFormatter(StringConverter<Date> value) {
        tickLabelFormatter.setValue(value);
    }

    public final ObjectProperty<StringConverter<Date>> tickLabelFormatterProperty() {
        return tickLabelFormatter;
    }

    private enum Interval {
        DECADE(Calendar.YEAR, 10),
        YEAR(Calendar.YEAR, 1),
        MONTH_6(Calendar.MONTH, 6),
        MONTH_3(Calendar.MONTH, 3),
        MONTH_1(Calendar.MONTH, 1),
        WEEK(Calendar.WEEK_OF_YEAR, 1),
        DAY(Calendar.DATE, 1),
        HOUR_12(Calendar.HOUR, 12),
        HOUR_6(Calendar.HOUR, 6),
        HOUR_3(Calendar.HOUR, 3),
        HOUR_1(Calendar.HOUR, 1),
        MINUTE_15(Calendar.MINUTE, 15),
        MINUTE_5(Calendar.MINUTE, 5),
        MINUTE_1(Calendar.MINUTE, 1),
        SECOND_15(Calendar.SECOND, 15),
        SECOND_5(Calendar.SECOND, 5),
        SECOND_1(Calendar.SECOND, 1),
        MILLISECOND(Calendar.MILLISECOND, 1);

        private final int amount;

        private final int interval;

        private Interval(int interval, int amount) {
            this.interval = interval;
            this.amount = amount;
        }
    }
}