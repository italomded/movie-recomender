package computacaoInteligente;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ReadFile {

	public Map<Integer, String> readFileMovies(String path) {
		String line;

		Map<Integer, String> map = new HashMap<>();

		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			while ((line = br.readLine()) != null) {
				String[] data = line.split(",");
				int key = Integer.parseInt(data[0]);
				String title = data[1];
				map.put(key, title);

			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return map;

	}

	public ArrayList<String> readFileRatings(String path) {
		String line;
		ArrayList<String> arrayList = new ArrayList<String>();

		try (BufferedReader br = new BufferedReader(new FileReader(path))) {
			while ((line = br.readLine()) != null) {
				arrayList.add(line);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return arrayList;

	}

}
