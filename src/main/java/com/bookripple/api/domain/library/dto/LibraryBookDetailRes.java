import com.bookripple.api.domain.library.enums.LibraryStatus;
import lombok.Builder;

import java.util.List;

@Builder
public record LibraryBookDetailRes(
        Long bookId,
        String title,
        String coverUrl,
        List<String> authors,
        String publisher,
        Integer totalPages,
        LibraryStatus status,     // READING / COMPLETED / LIKED 등
        Integer progressPercent,  // 0~100
        Integer currentPage,      // 있으면
        Integer totalPage         // 있으면 (혹은 totalPages로 통일)
) {}
