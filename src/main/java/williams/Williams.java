package williams;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.stream.Collectors;

/***
 * Compare ELO and SysParams. ELO format is csv file with 2 columns :
 * Code;Disable If Licensed SYSPARAMS format is csv
 * 
 * @author crabpie
 *
 */
public class Williams {

	private static final String WILLIAMS_ELO_FILENAME = "williamsElo.csv";

	private static final String TARGET_ELO_FILENAME = "targetElo.csv";

	private static final String WILLIAMS_SYS_FILENAME = "williamsSys.csv";

	private static final String TARGET_SYS_FILENAME = "targetSys.csv";

	public static void main(String[] args) {

		System.out.println("Comparing elo files ");
		Map<Object, Object> williamsElo = getMapFromCsvElo(WILLIAMS_ELO_FILENAME);
		Map<Object, Object> targetElo = getMapFromCsvElo(TARGET_ELO_FILENAME);
		compare(williamsElo, targetElo);

		System.out.println("Comparing system parameters files");
		Map<Object, Object> williamsSys = getMapFromCsvSysParams(WILLIAMS_SYS_FILENAME);
		Map<Object, Object> targetSys = getMapFromCsvSysParams(TARGET_SYS_FILENAME);
		compare(williamsSys, targetSys);

	}

	/**
	 * Compare maps, doing a diff, gives lines in williams not in target, lines
	 * in target not in williams, and lines in both but with different values
	 * 
	 * @param williams
	 * @param target
	 */
	private static void compare(Map<Object, Object> williams, Map<Object, Object> target) {

		// find all williams values not in file from target env
		Map<Object, Object> notInTarget = williams.entrySet().stream().filter(e -> !target.containsKey(e.getKey()))
				.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));
		// find all values from target env not in Williams sheet
		Map<Object, Object> notInWilliams = target.entrySet().stream().filter(e -> !williams.containsKey(e.getKey()))
				.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));

		// find all target values different from williams file
		Map<Object, Object> diffs = target.entrySet().stream().filter(e -> (williams.containsKey(e.getKey())))// only
				// ones
				// present
				// in
				// map1
				.filter(e -> !e.getValue().equals(williams.get(e.getKey()))) // only
																				// ones
																				// with
																				// different
																				// value
				.collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));

		if (diffs.isEmpty() && notInTarget.isEmpty() && notInWilliams.isEmpty()) {
			System.out.println("no differences");
		} else {
			notInTarget.entrySet().forEach(e -> System.out.println("williams value not found: " + e));

			notInWilliams.entrySet().forEach(e -> System.out.println("target value not found in williams sheet: " + e));

			diffs.entrySet().forEach(e -> System.out.println("target value different from williams value: " + e
					+ " , williams value = " + williams.get(e.getKey())));
		}

	}

	/***
	 * Return Map from csv
	 * 
	 * @param filename
	 * @return Map
	 */
	private static Map<Object, Object> getMapFromCsvElo(String filename) {

		Map<Object, Object> williamsElo = null;

		try (InputStream fileinput = new FileInputStream(filename);) {

			BufferedReader buffer = new BufferedReader(new InputStreamReader(fileinput));
			williamsElo = buffer.lines().skip(1).collect(Collectors.toMap(e -> e.split(";")[0], e -> e.split(";")[1]));

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return williamsElo;
	}

	/***
	 * Return Map from csv
	 * 
	 * @param filename
	 * @return Map
	 */
	private static Map<Object, Object> getMapFromCsvSysParams(String filename) {

		Map<Object, Object> williamsElo = null;

		try (InputStream fileinput = new FileInputStream(filename);) {

			BufferedReader buffer = new BufferedReader(new InputStreamReader(fileinput));
			williamsElo = buffer.lines().skip(1).map(e -> e.split(";"))// String[]
					.collect(Collectors.toMap(e -> getKey(e), e -> getValue(e)));

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return williamsElo;
	}

	/**
	 * 
	 * @param e
	 * @return
	 */
	private static Object getValue(String[] e) {

		if (e.length == 3) {
			return e[2];

		}

		return "";
	}

	/**
	 * 
	 * @param e
	 * @return
	 */
	private static Object getKey(String[] e) {
		if (e.length == 1) {
			return e[0];

		} else {
			return e[0] + e[1];

		}
	}

}
