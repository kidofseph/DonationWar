package background.util;

import java.text.DecimalFormat;

public class FormatUtil
{
	public static String getFormattedDollarAmount(Float p_fAmount)
	{
		DecimalFormat fmt = new DecimalFormat("#,###.00");
		return "$" + fmt.format(p_fAmount);
	}
}
