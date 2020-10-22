package el.core;

import java.io.Serializable;

public enum WorkerType implements Serializable {
    PROPOSER, ACCEPTER, LEARNER, SERVER
}
