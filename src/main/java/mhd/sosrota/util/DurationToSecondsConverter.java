package mhd.sosrota.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

/**
 *
 * @author Murilo Nunes <murilo_no@outlook.com>
 * @date 06/12/2025
 * @brief Class DurationConverter
 */
@Converter(autoApply = true)
public class DurationToSecondsConverter implements AttributeConverter<Duration, Long> {

    @Override
    public Long convertToDatabaseColumn(Duration duration) {
        if (duration == null) {
            return null;
        }
        return duration.get(ChronoUnit.SECONDS);
    }

    @Override
    public Duration convertToEntityAttribute(Long dbData) {
        if (dbData == null) {
            return null;
        }
        return Duration.ofSeconds(dbData);
    }
}

