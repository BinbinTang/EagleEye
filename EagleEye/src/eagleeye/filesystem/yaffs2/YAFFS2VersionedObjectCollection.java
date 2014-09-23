package eagleeye.filesystem.yaffs2;

import java.util.ArrayList;

public class YAFFS2VersionedObjectCollection extends ArrayList<YAFFS2VersionedObject>
{
	private static final long serialVersionUID = 1L;

	public boolean add(YAFFS2Object object)
	{
		for (YAFFS2VersionedObject versionedObject : this)
		{
			YAFFS2Object latestObject = versionedObject.getLatestVersion();
			
			if(latestObject.getId() == object.getId())
			{
				versionedObject.addVersion(object);
				return true;
			}
		}
		
		YAFFS2VersionedObject versionedObject = new YAFFS2VersionedObject();
		versionedObject.addVersion(object);
		
		this.add(versionedObject);
		
		return true;
	}
	
}