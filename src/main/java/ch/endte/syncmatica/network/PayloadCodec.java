package ch.endte.syncmatica.network;

import net.minecraft.util.Identifier;

public class PayloadCodec implements IPayloadType
{
    private final PayloadType type;
    private final String key;
    private final String namespace;
    private String path;
    private final Identifier id;

    protected PayloadCodec(PayloadType type, String key, String namespace, String path)
    {
        this.type = type;
        this.key = key;
        this.namespace = namespace;
        this.path = path;
        this.id = new Identifier(this.namespace, this.path);
    }
    public PayloadCodec(PayloadType type, String key, String namespace)
    {
        this.type = type;
        this.key = key;
        this.namespace = namespace;
        buildPath();
        this.id = new Identifier(this.namespace, this.path);
    }
    private void buildPath()
    {
        switch (this.type)
        {
            case SYNCMATICA_S2C -> this.path = "server_context";
            case SYNCMATICA_C2S -> this.path = "client_context";
            default -> this.path = "invalid";
        }
    }
    public PayloadType getType() { return this.type; }
    public String getKey() { return this.key; }
    public String getNamespace() { return this.namespace; }
    public String getPath() { return this.path; }
    public Identifier getId() { return this.id; }
}
