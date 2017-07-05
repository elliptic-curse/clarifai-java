package clarifai2.test;

import clarifai2.api.request.feedback.AddModelFeedbackRequest;

import clarifai2.api.request.feedback.Feedback;
import clarifai2.dto.feedback.ConceptFeedback;
import clarifai2.dto.feedback.FaceFeedback;
import clarifai2.dto.feedback.RegionFeedback;
import clarifai2.dto.input.Crop;
import org.junit.Test;

import java.util.Arrays;

public class AddModelFeedbackTests extends BaseClarifaiAPITest {

  @Test public void shouldFailWhenInvalidId() {
    AddModelFeedbackRequest request = client.addModelFeedback()
        .withId("@invalidId")
        .withImageUrl("@imageUrl")
        .withConcepts(
            ConceptFeedback.forIdAndValue("@conceptFeedbackId1", true),
            ConceptFeedback.forIdAndValue("@conceptFeedbackId2", false)
        )
        .withEndUserId("@endUserId")
        .withSessionId("@sessionId")
        .withEventType("annotation")
        .withOutputId("@outputId");
    assertFailure(request);
  }

  @Test public void shouldSucceedWhenValid() {
    AddModelFeedbackRequest request = client.addModelFeedback()
        .withId(client.getDefaultModels().travelModel().id())
        .withImageUrl(FAMILY_IMAGE_URL)
        .withConcepts(
            ConceptFeedback.forIdAndValue("car", true),
            ConceptFeedback.forIdAndValue("person", false)
        )
        .withEndUserId("@endUserId")
        .withSessionId("@sessionId")
        .withEventType("annotation")
        .withOutputId("@outputId");
    assertSuccess(request);
  }

  @Test public void shouldSucceedWhenValidRegions() {
    AddModelFeedbackRequest request = client.addModelFeedback()
        .withId(client.getDefaultModels().travelModel().id())
        .withImageUrl(FAMILY_IMAGE_URL)
        .withRegions(
            RegionFeedback.make(
                Crop.create().top(0.1f).bottom(0.2f).left(0.3f).right(0.4f),
                Feedback.ACCURATE
            )
                .withConceptFeedbacks(ConceptFeedback.forIdAndValue("car", false)))
        .withEndUserId("@endUserId")
        .withSessionId("@sessionId")
        .withEventType("annotation")
        .withOutputId("@outputId");
    assertSuccess(request);
  }

  @Test public void shouldSucceedWhenValidFace() {
    AddModelFeedbackRequest request = client.addModelFeedback()
        .withId(client.getDefaultModels().travelModel().id())
        .withImageUrl(FAMILY_IMAGE_URL)
        .withRegions(
            RegionFeedback.make(
                Crop.create().top(0.1f).bottom(0.2f).left(0.3f).right(0.4f),
                Feedback.ACCURATE
            )
                .withFaceFeedback(
                    FaceFeedback.make(
                        Arrays.asList(ConceptFeedback.forIdAndValue("face", true)),
                        Arrays.asList(ConceptFeedback.forIdAndValue("face", true))
                    )
                ))
        .withEndUserId("@endUserId")
        .withSessionId("@sessionId")
        .withEventType("annotation")
        .withOutputId("@outputId");
    assertSuccess(request);
  }
}
