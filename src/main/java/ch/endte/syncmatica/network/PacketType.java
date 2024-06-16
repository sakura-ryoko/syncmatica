package ch.endte.syncmatica.network;

import net.minecraft.util.Identifier;

public enum PacketType
{
    REGISTER_METADATA("syncmatica","register_metadata"),
    // one packet will be responsible for sending the entire metadata of a syncmatic
    // it marks the creation of a syncmatic - for now it also is responsible
    // for changing the syncmatic server and client side

    CANCEL_SHARE("syncmatica","cancel_share"),
    // send to a client when a share failed, 
    // the client can cancel the upload or upon finishing send a removal packet

    REQUEST_LITEMATIC("syncmatica","request_download"),
    // another group of packets will be responsible for downloading the entire
    // litematic starting with a download request

    SEND_LITEMATIC("syncmatica","send_litematic"),
    // a packet responsible for sending a bit of a litematic (16 kilobytes to be precise (half of what minecraft can send in one packet at most))

    RECEIVED_LITEMATIC("syncmatica","received_litematic"),
    // a packet responsible for triggering another send for a litematic
    // by waiting until a response is sent I hope we can ensure
    // that we do not overwhelm the clients' connection to the server

    FINISHED_LITEMATIC("syncmatica","finished_litematic"),
    // a packet responsible for marking the end of a litematic
    // transmission

    CANCEL_LITEMATIC("syncmatica","cancel_litematic"),
    // a packet responsible for cancelling an ongoing upload/download
    // will be sent in several cases - upon errors mostly

    REMOVE_SYNCMATIC("syncmatica","remove_syncmatic"),
    // a packet that will be sent to clients when a syncmatic got removed
    // send to the server by a client if a specific client intends to remove a litematic from the server

    REGISTER_VERSION("syncmatica","register_version"),
    // this packet will be sent to the client when it joins the server
    // upon receiving this packet the client will check the server version
    // initializes syncmatica on the clients end
    // if it can function with the version on the server then it will respond with a version of its own
    // if the server can handle the client version the server will send

    CONFIRM_USER("syncmatica","confirm_user"),
    // the confirm-user packet
    // send after a successful version exchange
    // fully starts up syncmatica on the clients end
    // sends all server placements along to the client

    FEATURE_REQUEST("syncmatica","feature_request"),
    // requests the partner to send a list of its features
    // does not require a fully finished handshake

    FEATURE("syncmatica","feature"),
    // sends feature set to the partner
    // send during a version exchange to check if the 2 versions are compatible, and there is no
    // default feature set available for the transmitted version
    // afterwards the feature set is used to communicate to the partner

    MODIFY("syncmatica","modify"),
    // sends updated placement data to the client or vice versa

    MODIFY_REQUEST("syncmatica","modify_request"),
    // send from client to server to request the editing of placement values
    // used to ensure that only one person can edit at a time thus preventing all kinds of stuff

    MODIFY_REQUEST_DENY("syncmatica","modify_request_deny"),
    MODIFY_REQUEST_ACCEPT("syncmatica","modify_request_accept"),

    MODIFY_FINISH("syncmatica","modify_finish"),
    // send from client to server to mark that the editing of placement values has concluded
    // sends along the final data of the placement

    MESSAGE("syncmatica","mesage");
    // sends a message from client to server - allows for future compatibility
    // can't fix the typo here lol

    public final Identifier identifier;

    PacketType(final String id, final String channel) { identifier = Identifier.of(id, channel); }

    public static boolean containsIdentifier(final Identifier id) {
        for (final PacketType p : PacketType.values()) {
            if (id.equals(p.identifier)) { // this took I kid you not 4-5 hours to find
                return true;
            }
        }
        return false;
    }

    public static boolean containsType(final PacketType type)
    {
        for (final PacketType p : PacketType.values())
        {
            if (type.equals(p))
            {
                return true;
            }
        }
        return false;
    }

    public static PacketType getType(final Identifier id)
    {
        for (final PacketType p : PacketType.values())
        {
            if (id.equals(p.identifier))
            {
                return p;
            }
        }
        return null;
    }

    public Identifier getId() { return this.identifier; }
}
