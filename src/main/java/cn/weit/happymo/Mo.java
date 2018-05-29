package cn.weit.happymo;


import cn.weit.happymo.enums.ResultEnum;
import cn.weit.happymo.exception.MoException;
import cn.weit.happymo.service.MoServer;

/**
 * @author weitong
 */
public final class Mo {

	private Mo() {
		throw new MoException(ResultEnum.INIT_ERROR);
	}


	public static MoServer moServerBuilder() {
		return new MoServer();
	}
}
