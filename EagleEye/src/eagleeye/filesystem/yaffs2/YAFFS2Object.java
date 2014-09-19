package eagleeye.filesystem.yaffs2;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

public class YAFFS2Object extends YAFFS2ObjectHeader implements MutableTreeNode
{
	protected int id;

	protected ArrayList<byte[]> dataChunks;
	
	protected boolean isUnlinked;
	
	protected boolean isDeleted;
	protected ArrayList<YAFFS2Object> children;
	protected YAFFS2Object parent;
	
	public ArrayList<YAFFS2Object> getChildren()
	{
		return children;
	}
	
	public void setChildren(ArrayList<YAFFS2Object> children)
	{
		this.children = children;
	}
		
	public YAFFS2Object()
	{
		this.id = -1;

		this.dataChunks = new ArrayList<>();
		this.isDeleted = false;
		this.isUnlinked = false;

		this.children = new ArrayList<YAFFS2Object>();
		this.parent = null;
	}
	public void addDataChunk(byte[] dataChunk)
	{
		this.dataChunks.add(dataChunk);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Enumeration<YAFFS2Object> children()
	{
		return (Enumeration<YAFFS2Object>) this.children;
	}
	
	@Override
	public boolean getAllowsChildren()
	{
		if (this.type == YAFFSObjectType.YAFFS_OBJECT_TYPE_DIRECTORY)
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

	public ArrayList<byte[]> getDataChunks()
	{
		return this.dataChunks;
	}

	public int getDataChunksByteSize()
	{
		int size = 0;

		for (byte[] bytes : this.dataChunks)
		{
			size += bytes.length;
		}

		return size;
	}

	public YAFFS2ObjectHeader getHeader()
	{
		return this;
	}
	
	@Override
	public int getIndex(TreeNode node)
	{
		return this.children.indexOf(node);
	}

	public int getId()
	{
		return this.id;
	}

	@Override
	public TreeNode getParent()
	{
		return this.parent;
	}
	
	public boolean isDeleted()
	{
		return this.isDeleted;
	}
	
	@Override
	public boolean isLeaf()
	{
		if (this.type == YAFFSObjectType.YAFFS_OBJECT_TYPE_FILE)
		{
			return true;
		}

		return false;
	}
	
	public boolean isUnlinked()
	{
		return this.isUnlinked;
	}
	
	public void prependDataChunk(byte[] dataChunk)
	{
		this.dataChunks.add(0, dataChunk);
	}
	
	public void setDataChunks(ArrayList<byte[]> dataChunks)
	{
		this.dataChunks = dataChunks;
	}
	
	public void setDeleted(boolean isDeleted)
	{
		this.isDeleted = isDeleted;
	}
	
	public void setHeader(YAFFS2ObjectHeader header)
	{
		Field[] fields = header.getClass().getDeclaredFields();

		for (Field field : fields)
		{
			try
			{
				header.getClass().getDeclaredField(field.getName()).set(this, field.get(header));
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	public void setId(int id)
	{
		this.id = id;
	}
	
	public void setUnlinked(boolean isUnlinked)
	{
		this.isUnlinked = isUnlinked;
	}

	@Override
	public void insert(MutableTreeNode child, int index)
	{
		this.children.add(index, (YAFFS2Object) child);
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
		
		YAFFS2Object objectParent = (YAFFS2Object) parent;
		
		objectParent.addChild(this);
	}
	
	public void addChild(YAFFS2Object newChild)
	{
		for (YAFFS2Object child : children)
		{
			if(child.name.equals(newChild.name))
			{
				this.remove(child);
				break;
			}
		}
		
		this.children.add(newChild);

		newChild.parent = this;
	}

	@Override
	public void setUserObject(Object arg0)
	{
		// TODO Auto-generated method stub
		
	}
	
}