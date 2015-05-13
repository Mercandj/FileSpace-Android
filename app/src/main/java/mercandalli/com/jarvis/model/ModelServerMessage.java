/**
 * Personal Project : Control server
 *
 * MERCANDALLI Jonathan
 */

package mercandalli.com.jarvis.model;

public class ModelServerMessage extends Model {

	private String content, id_conversation;

	public ModelServerMessage() {
		super();
	}

    public ModelServerMessage(String content) {
        super();
        this.content = content;
    }

    public ModelServerMessage(String content, String id_conversation) {
        super();
        this.content = content;
        this.id_conversation = id_conversation;
    }

    public boolean isConversationMessage() {
        if(this.id_conversation==null)
            return false;
        return !this.id_conversation.equals("");
    }

    public String getContent() {
        return content;
    }

    public String getId_conversation() {
        return id_conversation;
    }
}
