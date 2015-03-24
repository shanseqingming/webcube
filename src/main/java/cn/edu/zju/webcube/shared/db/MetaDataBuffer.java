package cn.edu.zju.webcube.shared.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import cn.edu.zju.webcube.shared.CubeTableDataSource;
import cn.edu.zju.webcube.shared.TableListDataSource;
import cn.edu.zju.webcube.shared.msg.CubeInfoMessage;
import cn.edu.zju.webcube.shared.msg.TableInfoMessage;

/**
 * In the client side, to simplfy the updates of UIs,
 * we buffer the meta information about database and cubes
 * @author wusai
 *
 */
public class MetaDataBuffer {
	
	private ArrayList<Table> tables;
	
	private HashMap<String, CubeManager> cubes;
	
	private HashMap<String, ArrayList<String>> newOrdinary;
	
	private HashMap<String, ArrayList<String>> newQuantity;
	
	private static MetaDataBuffer instance = null;
	
	private ArrayList<Table> tablesInNewCube = null; // dimension tables in new cube
	
	private Table factTable = null; //The fact table in new cube
	
	private CubeManager newCube = null;
	
	private final String tmpCubeName = "cube_0000_tmp";
	
	public static MetaDataBuffer getInstance(){
		if(instance==null){
			instance = new MetaDataBuffer();
		}
		return instance;
	}
	
	public MetaDataBuffer(){
		tables = new ArrayList<Table>();
		cubes = new HashMap<String, CubeManager>();
		newOrdinary = new HashMap<String, ArrayList<String>>();
		newQuantity = new HashMap<String, ArrayList<String>>();
	}
	
	/**
	 * parse the message to generate a new table
	 * @param tmessage
	 */
	public void acceptTableInfoMessage(String msg){
		TableInfoMessage tmessage = new TableInfoMessage(msg);
		Table  table = tmessage.getTable();
		tables.add(table);
	}
	
	/**
	 * parse the message to generate a cube
	 * @param cmessage
	 */
	public void acceptCubeInfoMessage(String msg){
		CubeInfoMessage cmessage = new CubeInfoMessage(msg);
		CubeManager cube = cmessage.getCube();
		cubes.put(cube.getID(), cube);
	}
	
	public ArrayList<Table> getAllTables(){
		return tables;
	}
	
	public HashMap<String, CubeManager> getCubes(){
		return cubes;
	}

	public HashMap<String, ArrayList<String>> getNewOrdinary() {
		return newOrdinary;
	}

	public HashMap<String, ArrayList<String>> getNewQuantity() {
		return newQuantity;
	}

	/**
	 * 
	 * @param name
	 * @return columns of the specific table
	 */
	public Table addTableToNewCube(String name){
		if(tablesInNewCube==null){
			tablesInNewCube = new ArrayList<Table>();
		}
		
		if(newCube!=null){
			newCube = new CubeManager(tmpCubeName, factTable);
		}
		
		for(Table table : tables){
			if(table.getName().equals(name)&& !tablesInNewCube.contains(table)){
				tablesInNewCube.add(table);
				return table;
			}
		}
		return null;
	}

	public void delTableToNewCube(String name){
		if(tablesInNewCube == null)
			return ;
		for(Table table : tables)
		{
			if(table.getName().equals(name) && tablesInNewCube.contains(table)) {
				tablesInNewCube.remove(table);
				return;
			}
		}
		return;
	}

	public void clearNewCube(){
		tablesInNewCube.clear();
		newOrdinary.clear();
		newQuantity.clear();
		newCube = null;
	}
	
	public void setFactTable(String tname){
		for(Table table : tables){
			if(table.getName().equals(tname)){
				factTable = table;
				if(newCube!=null){
					newCube.setFactTable(table);
				}
				break;
			}
		}		
	}
	
	public Table getFactTable(){
		return factTable;
	}
	
	public void cleanCube(){
		factTable = null;
		if(tablesInNewCube!=null)
			tablesInNewCube.clear();
		cubes.clear();
		newOrdinary.clear();
		newQuantity.clear();
		tables.clear();
		newCube = null;
		TableListDataSource.getInstance().clean();
		CubeTableDataSource.getInstance().clean();
	}
	
	public void setUpNewCube(String tname, ArrayList<String> ordinary, ArrayList<String> quantity){
		if(newCube == null)
			newCube = new CubeManager(tmpCubeName, factTable);
		
		//newCube.setAsOrdinaryColumn(tname, ordinary);
		//newCube.setAsQuantityColumn(tname, quantity);
		newOrdinary.put(tname, ordinary);
		newQuantity.put(tname, quantity);
	}
	
	public void insertOrdinaryForNewCube(String tname, String ordinary){
		if(newOrdinary.containsKey(tname)){
			ArrayList<String> ordinaryColumns = newOrdinary.get(tname);
			ordinaryColumns.add(ordinary);
		}
		else{
			ArrayList<String> ordinaryColumns = new ArrayList<String>();
			ordinaryColumns.add(ordinary);
			newOrdinary.put(tname, ordinaryColumns);
		}
	}
	
	public void deleteOrdinaryForNewCube(String tname, String ordinary){
		if(newOrdinary.containsKey(tname)){
			ArrayList<String> ordinaryColumns = newOrdinary.get(tname);
			ordinaryColumns.remove(ordinary);
		}
	}
	
	public void insertQuantityForNewCube(String tname, String quantity){
		if(newQuantity.containsKey(tname)){
			ArrayList<String> quantityColumns = newQuantity.get(tname);
			quantityColumns.add(quantity);
		}
		else{
			ArrayList<String> quantityColumns = new ArrayList<String>();
			quantityColumns.add(quantity);
			newQuantity.put(tname, quantityColumns);
		}
	}
	
	public void deleteQuantityForNewCube(String tname, String quantity){
		if(newQuantity.containsKey(tname)){
			ArrayList<String> quantityColumns = newQuantity.get(tname);
			quantityColumns.remove(quantity);
		}
	}
	
	public void addNewJoinCondition(String tname, String foreignkey, String alias){
		if(newCube == null)
			newCube = new CubeManager(tmpCubeName, factTable);
		
		for(Table table : tablesInNewCube){
			if(table.getName().equals(tname)){
				newCube.addDimensionTable(table, foreignkey, alias);
				break;
			}
		}
	}
	
	public String generateNewCube(String cubeName){
		if(newCube == null){
			newCube = new CubeManager(cubeName, factTable);
		}
		else
			newCube.updateCubeID(cubeName);
		
		int count = 0;
		Set<String> keys = newOrdinary.keySet();
		for(String next : keys){
			ArrayList<String> columns = newOrdinary.get(next);
			newCube.setAsOrdinaryColumn(next, columns);
			count += columns.size();
		}
		
		keys = newQuantity.keySet();
		for(String next : keys){
			ArrayList<String> columns = newQuantity.get(next);
			newCube.setAsQuantityColumn(next, columns);
			count += columns.size();
		}
		
		if(count>0)
			return newCube.generateCubeSQL();
		else
			return null;
	}
	
	public ArrayList<Table> getTableInNewCube(){
		return tablesInNewCube;
	}
	
	public boolean isCubeExist(String name){
		return cubes.containsKey(name); 
	}
	
	public CubeManager getNewCube(){
		return newCube;
	}
}