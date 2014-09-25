package eagleeye.filesystem.yaffs2;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

public class YAFFS2VersionedObject implements MutableTreeNode
{
	protected ArrayList<YAFFS2Object> versions = new ArrayList<>();

	protected ArrayList<YAFFS2VersionedObject> children = new ArrayList<>();
	protected YAFFS2VersionedObject parent;
	
	public YAFFS2Object getLatestVersion()
	{		
		return getVersion(versions.size() - 1);
	}

	public YAFFS2Object getFirstVersion()
	{
		return getVersion(0);
	}
	
	public YAFFS2Object getVersion(int version)
	{
		if(version < 0 || versions.size() <= version)
		{
			return null;
		}
		
		return versions.get(versions.size() - 1);
	}
	
	public void addVersion(YAFFS2Object object)
	{
		YAFFS2Object latestObject = this.getLatestVersion();
		
		if(latestObject != null)
		{
			HashMap<Integer, byte[]> data = latestObject.dataBlocks;
			int chunkCount = object.getFileSize() / 2048;
			
			while(-- chunkCount >= 0)
			{
				//((Object) object.dataBlocks).putIfAbsent(chunkCount, data.get(chunkCount));
			}
		}
		
		this.versions.add(object);
	}
	
	public int getVersionsCount()
	{
		return this.versions.size();
	}

	@Override
	public Enumeration<YAFFS2VersionedObject> children()
	{
		return java.util.Collections.enumeration(this.children);
	}
		
	@Override
	public boolean getAllowsChildren()
	{
		YAFFS2Object latestObject = this.getLatestVersion();
		
		if(latestObject == null)
		{
			return false;
		}
		
		if (latestObject.getType() == YAFFSObjectType.YAFFS_OBJECT_TYPE_DIRECTORY)
		{
			return true;
		}
		
		return false;
	}

	@Override
	public TreeNode getChildAt(int index)
	{
		if (this.children.size() > index)
		{
			return this.children.get(index);
		}

		return null;
	}

	@Override
	public int getChildCount()
	{
		return this.children.size();
	}

	@Override
	public int getIndex(TreeNode node)
	{
		return this.children.indexOf(node);
	}
	
	@Override
	public TreeNode getParent()
	{
		return this.parent;
	}
	
	@Override
	public boolean isLeaf()
	{
		YAFFS2Object latestObject = this.getLatestVersion();
		
		if (latestObject.getType() == YAFFSObjectType.YAFFS_OBJECT_TYPE_FILE)
		{
			return true;
		}

		return false;
	}

	@Override
	public void insert(MutableTreeNode child, int index)
	{
		this.children.add(index, (YAFFS2VersionedObject) child);
	}

	@Override
	public void remove(int index)
	{
		if(index > this.children.size())
		{
			this.children.remove(index);
		}
	}

	@Override
	public void remove(MutableTreeNode child)
	{
		this.remove(this.getIndex(child));
	}

	@Override
	public void removeFromParent()
	{
		this.parent.remove(this);
		this.parent = null;
	}

	@Override
	public void setParent(MutableTreeNode parent)
	{
		if(this.parent != null)
		{
			this.removeFromParent();
		}
		
		YAFFS2VersionedObject objectParent = (YAFFS2VersionedObject) parent;
		
		objectParent.addChild(this);
	}
	
	public void addChild(YAFFS2VersionedObject newChild)
	{		
		this.children.add(newChild);
		newChild.parent = this;
	}

	@Override
	public void setUserObject(Object arg0)
	{
		// TODO Auto-generated method stub
		
	}

	public String getRelativePath()
	{
		YAFFS2Object object = this.getLatestVersion();
		
		if(object == null)
		{
			return null;
		}
		
		if(this.parent == null)
		{
			return object.getName();
		}
		
		return this.parent.getRelativePath() + File.separator + object.getName();
	}
	
}