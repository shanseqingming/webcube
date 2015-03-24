package cn.edu.zju.webcube.server.mysql;

import cn.edu.zju.webcube.shared.db.CubeManager;
import cn.edu.zju.webcube.server.utils.Props;
import org.junit.Test;

/**
 * Created by zhifei on 2/7/15.
 */
public class TestMysqlMetaLoader {

	@Test
	public void TestGetCubeBuffer() {
		String propsFile = "./src/main/resources/config.properties";
		Props.initInstance(propsFile);

		MysqlMetaLoader loader = new MysqlMetaLoader("mysql");

		//HashMap<String, CubeManager> cubebuffer = loader.getAllCubeMeta();

		CubeManager cube = loader.getCubeMeta("pzftest3");

		cube.updateCubeID("test3");

		loader.saveCubeBuffer(cube);
	}

}
