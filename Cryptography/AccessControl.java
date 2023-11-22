package Cryptography;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class AccessControl {
    private HashMap<String, Set<String>> roles = new HashMap<>();
    private HashMap<String, String> users = new HashMap<>();

    public AccessControl(String filename) throws IOException {
        this.loadAccessControl(filename);
    }

    public void loadAccessControl(String filePath) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        boolean isRoleSection = false;

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("#")) {
                continue; // Skip empty lines and comments
            }

            if (line.startsWith("[Roles]")) {
                isRoleSection = true;
            } else if (line.startsWith("[Users]")) {
                isRoleSection = false;
            } else if (isRoleSection) {
                parseRole(line);
            } else {
                parseUser(line);
            }
        }
        resolveRoleInheritance();
    }

    public Set<String> getRights(String username) {
        return roles.get(users.get(username));
    }

    private void parseRole(String line) {
        String[] parts = line.split("=");
        String roleName = parts[0].trim();
        Set<String> permissions = new HashSet<>(Arrays.asList(parts[1].split(", ")));
        roles.put(roleName, permissions);
    }

    private void parseUser(String line) {
        String[] parts = line.split("=");
        users.put(parts[0].trim(), parts[1].trim());
    }

    private void resolveRoleInheritance() {
        roles.forEach((role, permissions) -> {
            Set<String> resolvedPermissions = new HashSet<>(permissions);
            permissions.forEach(permission -> {
                if (roles.containsKey(permission)) {
                    resolvedPermissions.addAll(roles.get(permission));
                }
            });
            roles.put(role, resolvedPermissions);
        });
    }
}
