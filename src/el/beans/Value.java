package el.beans;

import java.io.Serializable;
import java.util.UUID;

/**
 * Value contains UUID and Data
 */
public class Value implements Serializable {
    private UUID id;
    private byte[] data;

    public Value(UUID id, byte[] data) {
        super();
        this.id = id;
        this.data = data;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Value other = (Value) obj;
        if (id == null) {
            return other.id == null;
        } else return id.equals(other.id);
    }
}
