package com.example.eSmartRecruit.services;

import com.example.eSmartRecruit.exception.PositionException;
import com.example.eSmartRecruit.models.Position;


public interface IPositionService {
    public Position getSelectedPosition(int id) throws PositionException;
}
