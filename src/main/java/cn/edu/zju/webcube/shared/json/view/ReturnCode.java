package cn.edu.zju.webcube.shared.json.view;

public enum ReturnCode {
	SUCESS(0), METHOD_NOT_FOUND(1), PARA_ERROR(2), EMPTY_RESULT(3);
	private int code;

	ReturnCode(int code) {
		this.code = code;
	}

	@Override
	public String toString() {
		return String.valueOf(code);
	}

	public int getCode() {
		return code;
	}
}
