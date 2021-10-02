package org.ceiridge.intelligentconfig;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class IntelligentConfig {
	private File configFile;
	private ArrayList<ConfigContainer> containers;

	private boolean folderSystem;
	private File configFolder;
	private boolean transferred = false;

	public IntelligentConfig(File configFile, boolean load) throws NullPointerException, ClassCastException, IOException, EmptyNameException {
		this(configFile, load, false);
	}

	public IntelligentConfig(File configFile, boolean load, boolean folderSystem)
			throws NullPointerException, ClassCastException, IOException, EmptyNameException {
		this.configFile = configFile;
		this.containers = new ArrayList<ConfigContainer>();
		this.folderSystem = folderSystem;

		if (folderSystem) {
			String cName = configFile.getName();

			if (cName.contains(".")) {
				cName = cName.split(Pattern.quote("."))[0];
			}

			this.configFolder = new File(configFile.getParentFile(), cName);

			if (!this.configFolder.exists())
				this.configFolder.mkdirs();
		}

		if (load && configFile.exists()) {
			if (folderSystem) {
				transferConfigFile();
			} else
				load();
		}

		if (folderSystem && load && configFolder.exists() && !transferred) {
			load();
		}
	}

	public boolean isFolderSystem() {
		return folderSystem;
	}

	public File getConfigFolder() {
		return configFolder;
	}

	public IntelligentConfig(File configFile) throws NullPointerException, ClassCastException, IOException, EmptyNameException {
		this(configFile, true);
	}

	public File getConfigFile() {
		return configFile;
	}

	public ArrayList<ConfigContainer> getContainerList() {
		return containers;
	}

	public ConfigContainer getContainer(String containerName) throws EmptyNameException {
		for (ConfigContainer cont : containers) {
			if (cont.getName().trim().equalsIgnoreCase(containerName.trim()))
				return cont;
		}
		ConfigContainer cc = new ConfigContainer(containerName);
		containers.add(cc);
		return cc;
	}

	public void deleteContainer(String containerName) throws EmptyNameException {
		containers.remove(getContainer(containerName));
	}

	public void save() throws IOException {
		if (!folderSystem) {
			String fullSaveCode = "";
			for (ConfigContainer cont : containers) {
				fullSaveCode += cont.getSaveCode();
			}
			String realFullSaveCode = "";
			for (String saveLine : fullSaveCode.split("\n")) {
				if (saveLine.length() > 0) {
					realFullSaveCode += saveLine + "\n";
				}
			}
			realFullSaveCode = realFullSaveCode.substring(0, realFullSaveCode.length() - 1);
			Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.getConfigFile()), "UTF-8"));
			out.write(realFullSaveCode);
			out.close();
		} else {
			for (ConfigContainer contt : containers) {
				String fullSaveCode = contt.getSaveCode();

				String realFullSaveCode = "";
				for (String saveLine : fullSaveCode.split("\n")) {
					if (saveLine.length() > 0) {
						realFullSaveCode += saveLine + "\n";
					}
				}
				realFullSaveCode = realFullSaveCode.substring(0, realFullSaveCode.length() - 1);
				Writer out = new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream(new File(this.getConfigFolder(), contt.getName() + ".configcont")), "UTF-8"));
				out.write(realFullSaveCode);
				out.close();
			}
		}
	}

	private void transferConfigFile() throws NullPointerException, ClassCastException, IOException, EmptyNameException {
		load(true);
		for (ConfigContainer cont : containers) {
			File contF = new File(configFolder, cont.getName() + ".configcont");
			if (!contF.exists())
				contF.createNewFile();

			String fullSaveCode = cont.getSaveCode();
			String realFullSaveCode = "";
			for (String saveLine : fullSaveCode.split("\n")) {
				if (saveLine.length() > 0) {
					realFullSaveCode += saveLine + "\n";
				}
			}
			realFullSaveCode = realFullSaveCode.substring(0, realFullSaveCode.length() - 1);

			Files.write(contF.toPath(), realFullSaveCode.getBytes(Charset.forName("UTF-8")));
		}

		configFile.delete();
		transferred = true;
	}

	private void readFile(File file) throws NumberFormatException, NullPointerException, IOException, EmptyNameException {
		BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		String line;
		int openContainers = 0;
		ConfigContainer lastContainer = null;
		ConfigArray lastArray = null;

		while ((line = in.readLine()) != null) {

			if (line.startsWith("BEGINCONTAINER")) {
				openContainers++;

				String containerName = line.replaceFirst("BEGINCONTAINER ", "");

				if (lastContainer == null) {
					lastContainer = this.getContainer(containerName);
				} else {
					lastContainer = lastContainer.getContainer(containerName);
				}

			}

			if (line.startsWith("ENDCONTAINER") && openContainers > 0) {
				openContainers--;
				lastContainer = lastContainer.getParent();
			}

			if (line.startsWith("ENDARRAY") && openContainers > 0 && lastArray != null) {
				lastArray = null;
			}

			if (line.startsWith("BEGINARRAY") && openContainers > 0) {
				String type = line.split(" ")[1];
				lastArray = lastContainer.createArray(line.replaceFirst("BEGINARRAY " + type + " ", ""), ConfigVariableType.valueOf(type));
			}

			if (line.startsWith("!ARRVALUE") && lastContainer != null && lastArray != null) {
				switch (lastArray.getTypeClass().getName()) {
					case "java.lang.String":
						lastArray.add(line.replace("!ARRVALUE ", "").replace("{NEWL}", "\n"));
						break;
					case "java.lang.Integer":
						lastArray.add(Integer.parseInt(line.replace("!ARRVALUE ", "")));
						break;
					case "java.lang.Boolean":
						lastArray.add(Boolean.parseBoolean(line.replace("!ARRVALUE ", "")));
						break;
					case "java.lang.Float":
						lastArray.add(Float.parseFloat(line.replace("!ARRVALUE ", "")));
						break;
					case "java.lang.Double":
						lastArray.add(Double.parseDouble(line.replace("!ARRVALUE ", "")));
						break;
					case "java.lang.Long":
						lastArray.add(Long.parseLong(line.replace("!ARRVALUE ", "")));
						break;
					case "java.lang.Byte":
						lastArray.add(Byte.parseByte(line.replace("!ARRVALUE ", "")));
						break;
					default:
						break;
				}
			}

			if (line.startsWith("!") && lastContainer != null && lastArray == null) {
				String valName = line.split(" ")[1].replace("_", " ");
				String valuetype = line.split(" ")[0];

				switch (valuetype) {
					case "!STRVALUE":
						lastContainer.setString(valName, line.replace("!STRVALUE " + valName.replace(" ", "_") + " ", "").replace("{NEWL}", "\n"));
						break;
					case "!INTVALUE":
						lastContainer.setInteger(valName, Integer.parseInt(line.replace("!INTVALUE " + valName.replace(" ", "_") + " ", "")));
						break;
					case "!BOLVALUE":
						lastContainer.setBool(valName, Boolean.parseBoolean(line.replace("!BOLVALUE " + valName.replace(" ", "_") + " ", "")));
						break;
					case "!FLOVALUE":
						lastContainer.setFloat(valName, Float.parseFloat(line.replace("!FLOVALUE " + valName.replace(" ", "_") + " ", "")));
						break;
					case "!DOUVALUE":
						lastContainer.setDouble(valName, Double.parseDouble(line.replace("!DOUVALUE " + valName.replace(" ", "_") + " ", "")));
						break;
					case "!LONVALUE":
						lastContainer.setLong(valName, Long.parseLong(line.replace("!LONVALUE " + valName.replace(" ", "_") + " ", "")));
						break;
					case "!BYTEVALUE":
						lastContainer.setByte(valName, Byte.parseByte(line.replace("!BYTEVALUE " + valName.replace(" ", "_") + " ", "")));
						break;
					default:
						break;
				}
			}

		}
		in.close();
	}

	public void load() throws IOException, NullPointerException, ClassCastException, EmptyNameException {
		load(false);
	}

	private void load(boolean bypassFSystem) throws IOException, NullPointerException, ClassCastException, EmptyNameException {
		containers.clear();

		if (!folderSystem || bypassFSystem) {
			readFile(this.getConfigFile());
		} else {
			for (File contFF : this.getConfigFolder().listFiles()) {
				this.readFile(contFF);
			}
		}
	}
}
