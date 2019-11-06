package pictures;

public interface RegisteredROIObserver extends ROIObserver {

	Region getRegion();
	ROIObserver getObserver();
}
