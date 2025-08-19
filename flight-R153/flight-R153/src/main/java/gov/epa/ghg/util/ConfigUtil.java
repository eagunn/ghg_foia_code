package gov.epa.ghg.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigUtil {

	private static boolean listChartEnable;
	private static boolean barChartEnable;
	private static boolean pieChartEnable;
	private static boolean treeChartEnable;	

	static {
		try {
			InputStream propStream = Thread.currentThread()
					.getContextClassLoader().getResourceAsStream(
							"ghg.properties");
			Properties prop = new Properties();
			prop.load(propStream);
			listChartEnable = getBooleanProperty(prop, "ghg.chart.list.enable");
			barChartEnable = getBooleanProperty(prop, "ghg.chart.bar.enable");
			pieChartEnable = getBooleanProperty(prop, "ghg.chart.pie.enable");
			treeChartEnable = getBooleanProperty(prop, "ghg.chart.tree.enable");
		} catch (IOException e) {
			throw new IllegalArgumentException(
					"Could not load properties from ghg.properties");
		}
	}

	private static boolean getBooleanProperty(Properties prop, String key) {
		String value = prop.getProperty("ghg.chart.list.enable");
		if (value.equals("true")) {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean isListChartEnable() {
		return listChartEnable;
	}

	public static void setListChartEnable(boolean listChartEnable) {
		ConfigUtil.listChartEnable = listChartEnable;
	}

	public static boolean isBarChartEnable() {
		return barChartEnable;
	}

	public static void setBarChartEnable(boolean barChartEnable) {
		ConfigUtil.barChartEnable = barChartEnable;
	}

	public static boolean isPieChartEnable() {
		return pieChartEnable;
	}

	public static void setPieChartEnable(boolean pieChartEnable) {
		ConfigUtil.pieChartEnable = pieChartEnable;
	}

	public static boolean isTreeChartEnable() {
		return treeChartEnable;
	}

	public static void setTreeChartEnable(boolean treeChartEnable) {
		ConfigUtil.treeChartEnable = treeChartEnable;
	}
}