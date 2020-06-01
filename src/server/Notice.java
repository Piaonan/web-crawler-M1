package server;

public class Notice {
	public enum Link {
		Unexplored, Ongoing, Explored;

		public boolean is(Notice n) {
			return this == n.link;
		}
	}

	private Link link;
	private String message = "\n";

	public Notice(Link newState) {
		link = newState;
	}

	public Notice(String msg) {
		link = Link.Explored;
		message = " (" + msg.replaceAll("_", " ") + ")\n";
	}
	
	public Notice(String state, String msg) {
		link = Link.valueOf(state);
		message = msg;
	}

	public boolean check(Link state) {
		return link == state;
	}

	public String getNotice() {
		return message;
		/*if (link == Link.Explored)
			return " => exploré" + message;
		else if (link == Link.Unexplored)
			return " => inexploré" + message;
		else
			return " => en cours" + message;*/
	}
	
	public String getMessage() {
		return message;
	}
	
	public String getLinkState() {
		return link.toString();
	}
}
