package el.core;

import el.beans.Value;

/**
 * 数据实例类
 *
 * @author xiaoxiang
 * @date 2020/10/23
 */
public class Instance {
    // current ballot number
    private int ballot;
    // accepted value
    private Value value;
    // accepted value's ballot
    private int acceptedBallot;

    public Instance(int ballot, Value value, int acceptedBallot) {
        super();
        this.ballot = ballot;
        this.value = value;
        this.acceptedBallot = acceptedBallot;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    public int getBallot() {
        return ballot;
    }

    public void setBallot(int ballot) {
        this.ballot = ballot;
    }

    public Value getValue() {
        return value;
    }

    public int getAcceptedBallot() {
        return acceptedBallot;
    }

    public void setAcceptedBallot(int acceptedBallot) {
        this.acceptedBallot = acceptedBallot;
    }
}
