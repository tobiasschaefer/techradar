package radar.item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ClusterInfoMap {
	
	private Map<String, ClusterInfo> clusterInfos = new HashMap<>();
	
	public void copyInto (ClusterInfoMap cim) {
		List<ClusterInfo> l = new ArrayList<>();
		l.addAll(cim.clusterInfos.values());
		cim.clusterInfos.clear();
		for (Entry<String, ClusterInfo> e : clusterInfos.entrySet()) {
			ClusterInfo ci;
			if (l.size() > 0) {
				ci = l.remove(l.size() - 1);
			} else {
				ci = new ClusterInfo();
			}
			e.getValue().copyInto(ci);
			cim.clusterInfos.put(e.getKey(), ci);
		}
	}

	public ClusterInfo get(String clusterValue) {
		ClusterInfo ci = clusterInfos.get(clusterValue);
		if (ci == null) {
			ci = new ClusterInfo();
			ci.setName(clusterValue);
			clusterInfos.put(clusterValue, ci);
		}
		return ci;
	}
	
	public ClusterInfo add(Item item) {
		ClusterInfo ci = get(item.getClusterValue());
		ci.add(item);
		return ci;
	}
	
	public void remove(Item item) {
		ClusterInfo ci = get(item.getClusterValue());
		ci.remove(item);
	}
	
	public Map<String, ClusterInfo> getClusterInfos() {
		return clusterInfos;
	}
	
	public void clearAddWinkelTmp() {
		for (ClusterInfo ci : clusterInfos.values()) {
			ci.clearAddWinkelTmp();
		}
	}
	
}
