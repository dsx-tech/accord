package uk.dsx;

public interface Client {

    <C extends Client> C connect() throws InstantiationException;

    <C extends Client> C send(String sourcePath, String targetPath);

    <C extends Client> C get(String sourcePath, String targetPath);

    <C extends Client> C exec(String command);

    <C extends Client> C close();

}
