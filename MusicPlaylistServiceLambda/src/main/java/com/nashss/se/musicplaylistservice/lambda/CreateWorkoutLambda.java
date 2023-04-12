package com.nashss.se.musicplaylistservice.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.nashss.se.musicplaylistservice.activity.requests.CreateWorkoutRequest;


public class CreateWorkoutLambda
        extends LambdaActivityRunner<CreateWorkoutRequest, CreateWorkoutResult>
        implements RequestHandler<AuthenticatedLambdaRequest<CreateWorkoutRequest>, LambdaResponse> {

    @Override
    public LambdaResponse handleRequest(AuthenticatedLambdaRequest<CreateWorkoutRequest> input, Context context) {
        return super.runActivity(
                () -> {
                    CreateWorkoutRequest unauthenticatedRequest = input.fromBody(CreateWorkoutRequest.class);
                    return input.fromUserClaims(claims ->
                            CreateWorkoutRequest.builder()
                                    .withDate(unauthenticatedRequest.getDate())
                                    .withWorkoutType(unauthenticatedRequest.getWorkoutType())
                                    .withDurationInHours(unauthenticatedRequest.getDurationInHours())
                                    .withDurationInMinutes(unauthenticatedRequest.getDurationInMinutes())
                                    .withDurationInSeconds(unauthenticatedRequest.getDurationInSeconds())
                                    .withDistance(unauthenticatedRequest.getDistance())
                                    .withCustomerId(claims.get("email"))
                                    .build());
                },
                (request, serviceComponent) ->
                        serviceComponent.provideCreateWorkoutActivity().handleRequest(request)
        );
    }
}