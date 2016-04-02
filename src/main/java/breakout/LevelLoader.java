package breakout;

import java.io.File;
import java.util.HashMap;
import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Elements;

/*
This class loads levels preferences, they are located
in file "levels.xml" in "resource" directory using "XOM" third-party library.
"levelData" object holds preferences for one(current) level
*/
class LevelLoader {
	// Holds preferences for one level
	private HashMap<String, String> levelData = 
			new HashMap<String, String>();
	// Represents all levels that can be used
	private Elements levels;
	// Represents current level number
	private int currentLevel = 0;
	LevelLoader() {
		loadLevels();
	}
	/*
	Loads "levels.xml" in memory and gets
	all levels that are available
	*/
	private void loadLevels() {
		try {
			//Document doc = new Builder().build(
			//		new File("resources/levels.xml"));
			Document doc = new Builder().build(
					getClass().getResourceAsStream("/resources/levels.xml"));
			levels = doc.getRootElement().getChildElements();
		}	catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	// Checks if one more level is available
	public boolean hasNextLevel() {
		try {
			Element level = levels.get(currentLevel);
		}	catch(IndexOutOfBoundsException e) {
			return false;
		}
		return true;
	}
	/*
	Fills preferences from current level into 
	HashMap object
	KEY = xml tag, VALUE = xml tag value
	*/
	public HashMap<String, String> nextLevel() {
		levelData.clear();
		try {
			Element level = levels.get(currentLevel++);
			Elements data = level.getChildElements();
			for(int x = 0; x < data.size(); x++) {
				levelData.put(data.get(x).getLocalName(),
						data.get(x).getValue());
			}
		}	catch(IndexOutOfBoundsException e) {
			return null;
		}
		return levelData;
	}
	// Set current level to first
	public void resetLevels() {
		currentLevel = 0;
	}
	public void setLevel(int nOfLevel) {
		currentLevel = nOfLevel;
	}
	public int getLevel() {
		return currentLevel;
	}
}