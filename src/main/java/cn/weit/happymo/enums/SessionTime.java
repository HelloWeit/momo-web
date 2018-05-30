package cn.weit.happymo.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author weitong
 */

@Getter
public enum SessionTime {
	NORMAL(1),
	SPECIAL(7*24),
	ERROR(-1),

	;
	private long time;

	SessionTime(long time) {
		this.time = time;
	}

	public SessionTime getEnumByValue(long value) {
		return Arrays.stream(SessionTime.values())
				.filter(sessionTime -> sessionTime.getTime()==value)
				.findFirst()
				.orElse(SessionTime.ERROR);
	}
}
