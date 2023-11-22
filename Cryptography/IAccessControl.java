package Cryptography;

import java.io.IOException;
import java.util.Set;

public interface IAccessControl {
    public void loadAccessControl(String filePath) throws IOException;

    public Set<String> getRights(String username);
}
