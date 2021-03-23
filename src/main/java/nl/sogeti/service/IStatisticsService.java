package nl.sogeti.service;

public interface IStatisticsService {

    IStatisticsService.ISimpleFramerateMeter getSimpleFrameRateMeter();

    interface ISimpleFramerateMeter {

        double getFrameRate();
    }
}
