package el.exception;

public class PaxosClientNullAddressException extends Exception {
	public PaxosClientNullAddressException() {
		super("paxos client remote side address is null");
	}
}
