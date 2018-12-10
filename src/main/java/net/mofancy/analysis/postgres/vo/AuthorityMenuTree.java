package net.mofancy.analysis.postgres.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;

/**
 * ${DESCRIPTION}
 *
 * @author wanghaobin
 * @create 2017-06-19 13:03
 */
public class AuthorityMenuTree extends TreeNode implements Serializable{
    String label;
    List<AuthorityMenuTree> nodes = new ArrayList<AuthorityMenuTree>();
    String icon;
    List<Map<String,Object>> elements = new ArrayList<Map<String,Object>>();

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public AuthorityMenuTree(String label,String icon,List<Map<String,Object>> elements, List<AuthorityMenuTree> nodes) {
        this.label = label;
        this.icon = icon;
        this.elements = elements;
        this.nodes = nodes;
    }

    public AuthorityMenuTree() {
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

	public List<AuthorityMenuTree> getNodes() {
        return nodes;
    }

    public void setNodes(List<AuthorityMenuTree> nodes) {
        this.nodes = nodes;
    }

    public List<Map<String, Object>> getElements() {
		return elements;
	}

	public void setElements(List<Map<String, Object>> elements) {
		this.elements = elements;
	}

	@Override
    public void setChildren(List<TreeNode> children) {
        super.setChildren(children);
        nodes = new ArrayList<AuthorityMenuTree>();
    }

    @Override
    public void add(TreeNode node) {
        super.add(node);
        AuthorityMenuTree n = new AuthorityMenuTree();
        BeanUtils.copyProperties(node,n);
        nodes.add(n);
    }
}
