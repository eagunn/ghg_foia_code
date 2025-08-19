package org.jstripe.model;

public class GarbageCollector {
    private String name;
    private long collectionCount = 0;
    private String collectionTime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

	public long getCollectionCount() {
		return collectionCount;
	}

	public void setCollectionCount(long collectionCount) {
		this.collectionCount = collectionCount;
	}

	public String getCollectionTime() {
		return collectionTime;
	}

	public void setCollectionTime(String collectionTime) {
		this.collectionTime = collectionTime;
	}
}
