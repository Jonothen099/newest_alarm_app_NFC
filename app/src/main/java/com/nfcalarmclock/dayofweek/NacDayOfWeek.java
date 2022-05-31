package com.nfcalarmclock.dayofweek;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.nfcalarmclock.R;
import com.nfcalarmclock.shared.NacSharedConstants;
import com.nfcalarmclock.system.NacCalendar;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * A button that consists of an image to the left, and text to the right of it.
 */
@SuppressWarnings({"UnnecessaryInterfaceModifier", "UnusedReturnValue"})
public class NacDayOfWeek
	implements NacDayButton.OnDayChangedListener
{

	/**
	 * Listen for when the day of week is changed.
	 *
	 * Returning True means that the listener handled the event, and False means
	 * that the NacDayOfWeek class will handle the click event, and execute the
	 * default action.
	 */
	public interface OnWeekChangedListener
	{
		@SuppressWarnings("SameReturnValue")
        public boolean onWeekChanged(NacDayButton button, NacCalendar.Day day);
	}

	/**
	 * Day of week view.
	 */
	private final LinearLayout mDayOfWeekView;

	/**
	 * Day of week changed listener.
	 */
	private NacDayOfWeek.OnWeekChangedListener mOnWeekChangedListener;

	/**
	 */
	public NacDayOfWeek(LinearLayout view)
	{
		this.mDayOfWeekView = view;
		this.mOnWeekChangedListener = null;

		this.setupDayButtons();
	}

	/**
	 * Convert a particular day to its corresponding view ID.
	 */
	private int dayToId(NacCalendar.Day day)
	{
		switch (day)
		{
			case SUNDAY:
				return R.id.dow_sun;
			case MONDAY:
				return R.id.dow_mon;
			case TUESDAY:
				return R.id.dow_tue;
			case WEDNESDAY:
				return R.id.dow_wed;
			case THURSDAY:
				return R.id.dow_thu;
			case FRIDAY:
				return R.id.dow_fri;
			case SATURDAY:
				return R.id.dow_sat;
			default:
				break;
		}
		return 0;
	}

	/**
	 * @return The day button given a particular day.
	 */
	public NacDayButton getDayButton(NacCalendar.Day day)
	{
		LinearLayout view = this.getDayOfWeekView();
		int id = this.dayToId(day);
		return view.findViewById(id);
	}

	/**
	 * @return A list of all the day buttons.
	 */
	public List<NacDayButton> getDayButtons()
	{
		LinearLayout view = this.getDayOfWeekView();
		int count = view.getChildCount();
		List<NacDayButton> buttons = new ArrayList<>();

		for (int i=0; i < count; i++)
		{
			NacDayButton b = (NacDayButton) view.getChildAt(i);
			buttons.add(b);
		}

		return buttons;
	}

	/**
	 * @return The day of week view.
	 */
	public LinearLayout getDayOfWeekView()
	{
		return this.mDayOfWeekView;
	}

	/**
	 * @return The alarm days.
	 */
	public EnumSet<NacCalendar.Day> getDays()
	{
		EnumSet<NacCalendar.Day> days = NacCalendar.Day.none();

		for (NacCalendar.Day d : NacCalendar.WEEK)
		{
			if (this.isDayEnabled(d))
			{
				days.add(d);
			}
		}

		return days;
	}

	/**
	 * @return The day of week on click listener.
	 */
	public NacDayOfWeek.OnWeekChangedListener getOnWeekChangedListener()
	{
		return this.mOnWeekChangedListener;
	}

	/**
	 * Convert an view ID to its corresponding day.
	 */
	private NacCalendar.Day idToDay(int id)
	{
		if (id == R.id.dow_sun)
		{
			return NacCalendar.Day.SUNDAY;
		}
		else if (id == R.id.dow_mon)
		{
			return NacCalendar.Day.MONDAY;
		}
		else if (id == R.id.dow_tue)
		{
			return NacCalendar.Day.TUESDAY;
		}
		else if (id == R.id.dow_wed)
		{
			return NacCalendar.Day.WEDNESDAY;
		}
		else if (id == R.id.dow_thu)
		{
			return NacCalendar.Day.THURSDAY;
		}
		else if (id == R.id.dow_fri)
		{
			return NacCalendar.Day.FRIDAY;
		}
		else if (id == R.id.dow_sat)
		{
			return NacCalendar.Day.SATURDAY;
		}
		else
		{
			return null;
		}
	}

	/**
	 * @return True if the button is enabled and false if it is not.
	 */
	public boolean isDayEnabled(NacCalendar.Day day)
	{
		NacDayButton button = this.getDayButton(day);
		return button.getButton().isChecked();
		//Why was I using isEnabled()? That's a method from View.
		//return button.isEnabled();
	}

	/**
	 */
	@Override
	public void onDayChanged(NacDayButton button)
	{
		int id = button.getId();
		NacCalendar.Day day = this.idToDay(id);
		NacDayOfWeek.OnWeekChangedListener listener =
			this.getOnWeekChangedListener();

		if (listener != null)
		{
			listener.onWeekChanged(button, day);
		}
	}

	/**
	 * Set the days that will be enabled/disabled.
	 *
	 * TODO This is doing more animating than necessary.
	 *      Only enable/disable if needs to be done.
	 *
	 * @param  days  The button days that will be enabled.
	 */
	public void setDays(EnumSet<NacCalendar.Day> days)
	{
		for (NacCalendar.Day d : NacCalendar.WEEK)
		{
			NacDayButton button = this.getDayButton(d);
			button.setOnDayChangedListener(null);

			if (days.contains(d))
			{
				button.enable();
			}
			else
			{
				button.disable();
			}

			button.setOnDayChangedListener(this);
		}
	}

	/**
	 * @see #setDays(EnumSet)
	 */
	public void setDays(int value)
	{
		EnumSet<NacCalendar.Day> days = NacCalendar.Days.valueToDays(value);
		this.setDays(days);
	}

	/**
	 * Set the day to start week on.
	 */
	public void setStartWeekOn(int start)
	{
		LinearLayout view = this.getDayOfWeekView();
		NacDayButton sunday = this.getDayButton(NacCalendar.Day.SUNDAY);
		NacDayButton monday = this.getDayButton(NacCalendar.Day.MONDAY);
		View firstChild = view.getChildAt(0);
		View lastChild = view.getChildAt(6);

		if (start == 1)
		{
			if (firstChild.getId() != monday.getId())
			{
				view.removeView(firstChild);
				view.addView(firstChild, 6);
			}
		}
		else
		{
			if (firstChild.getId() != sunday.getId())
			{
				view.removeView(lastChild);
				view.addView(lastChild, 0);
			}
		}
	}

	/**
	 * Set the listener for the week is changed.
	 */
	public void setOnWeekChangedListener(
		NacDayOfWeek.OnWeekChangedListener listener)
	{
		this.mOnWeekChangedListener = listener;
	}

	/**
	 * Setup the day buttons.
	 */
	protected void setupDayButtons()
	{
		LinearLayout view = this.getDayOfWeekView();
		Context context = view.getContext();
		NacSharedConstants cons = new NacSharedConstants(context);
		List<String> week = cons.getDaysOfWeek();
		int count = view.getChildCount();

		for (int i=0; i < count; i++)
		{
			NacDayButton button = (NacDayButton) view.getChildAt(i);
			button.setText(week.get(i));
			button.setOnDayChangedListener(this);
		}
	}

}
