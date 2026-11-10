package com.example

import com.example.model.VideoLesson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ExampleRobolectricTest {

  @Test
  fun testVideoLessonModel() {
    val lesson = VideoLesson(
      id = "les_test",
      title = "محاضرة تجريبية",
      subject = "الرياضيات",
      chapter = "الفصل 1",
      description = "شرح تفصيلي",
      durationMinutes = 50,
      videoUrl = "https://example.com/video.mp4"
    )
    assertNotNull(lesson)
    assertEquals(50, lesson.durationMinutes)
    assertEquals("محاضرة تجريبية", lesson.title)
  }
}
