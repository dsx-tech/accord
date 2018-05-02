package uk.dsx.accord.common;

import lombok.Data;

import java.net.URL;

@Data
public abstract class AbstractInstance implements Instance {

    public abstract void uploadFile(String source, String target);

    public abstract void uploadFile(URL source);
}
