package com.nashss.se.musicplaylistservice.activity;

import com.nashss.se.musicplaylistservice.activity.requests.GetFullWorkoutHistoryByCustomerRequest;
import com.nashss.se.musicplaylistservice.activity.results.GetFullWorkoutHistoryByCustomerResult;
import com.nashss.se.musicplaylistservice.converters.ModelConverter;
import com.nashss.se.musicplaylistservice.dynamodb.WorkoutDao;
import com.nashss.se.musicplaylistservice.dynamodb.models.Triathlon;
import com.nashss.se.musicplaylistservice.dynamodb.models.TriathlonComparator;
import com.nashss.se.musicplaylistservice.exceptions.InvalidAttributeValueException;
import com.nashss.se.musicplaylistservice.models.PlaylistModel;
import com.nashss.se.musicplaylistservice.models.WorkoutModel;
import com.nashss.se.musicplaylistservice.utils.CollectionUtils;
import com.nashss.se.projectresources.music.playlist.servic.util.MusicPlaylistServiceUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.fasterxml.jackson.databind.type.LogicalType.Collection;

public class GetFullWorkoutHistoryByCustomerActivity {

    private final Logger log = LogManager.getLogger();
    private final WorkoutDao workoutDao;

    /**
     * Instantiates a new GetFullWorkoutHistoryByCustomerActivity object
     *
     * @param workoutDao WorkoutDao to access the Triathlon table
     */
    @Inject
    public GetFullWorkoutHistoryByCustomerActivity(WorkoutDao workoutDao) { this.workoutDao = workoutDao; }

    /**
     * This method handles the incoming request by querying a list of all
     * workouts for the specified customerId.
     * <p>
     * If the provided customer ID has invalid characters, throws an
     * InvalidAttributeValueException
     *
     * @param getFullWorkoutHistoryByCustomerRequest request object containing the playlist name and customer ID
     *                              associated with it
     * @return getFullWorkoutHistoryByCustomerResult result object containing the API defined {@link PlaylistModel}
     */
    public GetFullWorkoutHistoryByCustomerResult handleRequest(final GetFullWorkoutHistoryByCustomerRequest
                                                                       getFullWorkoutHistoryByCustomerRequest) {
        log.info("Received GetFullWorkoutHistoryByCustomerRequest {}", getFullWorkoutHistoryByCustomerRequest);

        // pull list of workouts from DDB for given customer ID
        String customerId = getFullWorkoutHistoryByCustomerRequest.getCustomerId();
        List<Triathlon> triathlonList = workoutDao.getAllTriathlonRecordsForCustomer(customerId);

        // copy paginated query list received from DDB into an arraylist so that it can be sorted
        List<Triathlon> triathlonListCopy =
                triathlonList != null ? CollectionUtils.copyToList(triathlonList) : new ArrayList<>();

        // comparator to sort from most recent to oldest by date
        Comparator<Triathlon> triathlonComparator = new TriathlonComparator().reversed();
        triathlonListCopy.sort(triathlonComparator);

        // convert to client model list, package in result and return
        List<WorkoutModel> workoutModels = new ModelConverter().toWorkoutModels(triathlonListCopy);

        return GetFullWorkoutHistoryByCustomerResult.builder()
                .withTriathlonList(workoutModels)
                .build();
    }
}
