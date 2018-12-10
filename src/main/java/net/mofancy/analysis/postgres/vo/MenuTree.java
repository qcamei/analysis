package net.mofancy.analysis.postgres.vo;

/**
 * Created by Ace on 2017/6/12.
 */
public class MenuTree extends TreeNode {
    String icon;
    String title;
    String href;
    boolean spread = false;
    String code;
    String enabled;
    String description;

    public MenuTree() {
    }

    public MenuTree(int id, String name, int parentId,String code,String enabled,String description) {
        this.id = id;
        this.parentId = parentId;
        this.title = name;
        this.code = code;
        this.enabled = enabled;
        this.description = description;
    }
    public MenuTree(int id, String name, MenuTree parent) {
        this.id = id;
        this.parentId = parent.getId();
        this.title = name;
    }
    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public boolean isSpread() {
        return spread;
    }

    public void setSpread(boolean spread) {
        this.spread = spread;
    }

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getEnabled() {
		return enabled;
	}

	public void setEnabled(String enabled) {
		this.enabled = enabled;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
    
}
