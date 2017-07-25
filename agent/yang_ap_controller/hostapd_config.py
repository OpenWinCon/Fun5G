





class hostapd_config:
    def __init__(self):


    def __str__(self):
        return '[{}]'.format(', '.join(str(i) for i in self.vector))

    def __len__(self):
        return len(self.vector)

    def extend(self, newLen):
        self.vector.append([0] * (newLen - len(self)) )
